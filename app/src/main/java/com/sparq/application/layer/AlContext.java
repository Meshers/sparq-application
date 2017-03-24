package com.sparq.application.layer;

import android.util.Log;

import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.almessage.AlQuestion;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.ThreadPdu;
import com.sparq.application.userinterface.model.ConversationThread;
import com.sparq.application.userinterface.model.UserItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlContext {

    private static final String TAG = "AlContext";

    private static final int SESSION_ID = 1;

    private boolean mBusy;
    private ApplicationLayerPdu mCurrentPdu;
    private Callback mCallback;
    private long timeSinceLastMessage;
    private byte mLastThreadId;
    private byte mCreatorId;
    private HashMap<AlQuestion, ArrayList<AlAnswer>> mSessionThreads;

    public interface Callback {
        void transmitPdu(ApplicationLayerPdu pdu);

        void sendUpperLayer(ApplicationLayerPdu.TYPE type, AlMessage message);
    }

    private final byte mSessionId;

    public AlContext(byte sessionId, Callback callback){
        this.mSessionId = SESSION_ID;
        this.mCallback = callback;

        mSessionThreads = new HashMap<>();

    }

    public AlQuestion getAlQuestion(byte creatorId, byte threadId){

        for(AlQuestion question: mSessionThreads.keySet()){

            byte[] combinedQuestionId = question.getCombinedQuestionId();

            if(combinedQuestionId[0] == creatorId && combinedQuestionId[1] == threadId){
                return question;
            }
        }
        return null;
    }

    public AlAnswer getAlAnswer(ArrayList<AlAnswer> answers, byte subThreadCreatorId, byte subThreadId){

        for(AlAnswer answer: answers){

            byte[] combinedAnswerId = answer.getCombinedAnswerId();

            if(combinedAnswerId[0] == subThreadCreatorId && combinedAnswerId[1] == subThreadId){
                return answer;
            }
        }
        return null;
    }

    public void sendThreadPdu(ApplicationLayerPdu.TYPE type, byte threadCreatorId, byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data){

        ApplicationLayerPdu pdu = null;
        AlQuestion retrievedQuestion;
        AlAnswer retrievedAnswer;

        try{
            switch(type){
                case QUESTION:

                    AlQuestion question = new AlQuestion(threadCreatorId, threadId, data);
                    mSessionThreads.put(question, new ArrayList<AlAnswer>());

                    pdu = ThreadPdu.getQuestionPdu(threadCreatorId, threadId, data);
                    break;
                case ANSWER:

                    AlAnswer answer = new AlAnswer(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);

                    //get the corresponding answer array
                    retrievedQuestion = getAlQuestion(threadCreatorId, threadId);
                    if(retrievedQuestion != null){
                        mSessionThreads.get(retrievedQuestion).add(answer);
                    }
                    else{
                        throw new IllegalArgumentException("No question exists for such an answer");
                    }

                    pdu = ThreadPdu.getAnswerPdu(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);
                    break;
                case QUESTION_VOTE:

                    AlVote questionVote = AlVote.getQuestionVote(threadCreatorId, threadId, data);

                    retrievedQuestion = getAlQuestion(threadCreatorId, threadId);
                    if(retrievedQuestion != null){
                        retrievedQuestion.addVote(questionVote);
                    }
                    else{
                        throw new IllegalArgumentException("No question exists for such a vote");
                    }

                    pdu = ThreadPdu.getQuestionVotePdu(threadCreatorId, threadId, data);
                    break;
                case ANSWER_VOTE:

                    AlVote answerVote = AlVote.getAnswerVote(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);

                    retrievedQuestion = getAlQuestion(threadCreatorId, threadId);
                    if(retrievedQuestion != null){
                        retrievedAnswer = getAlAnswer(
                                mSessionThreads.get(retrievedQuestion),
                                subThreadCreatorId,
                                subThreadId
                        );

                        if(retrievedAnswer != null){
                            retrievedAnswer.addVote(answerVote);
                        }
                        else{
                            throw new IllegalArgumentException("No answer exists for such a vote");
                        }

                    }
                    else{
                        throw new IllegalArgumentException("No question exists for such a vote");
                    }

                    pdu = ThreadPdu.getAnswerVotePdu(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);
                    break;
                default:
                    throw new IllegalArgumentException("No such type of Thread packet exists");
            }

        }
        catch(IllegalArgumentException e){
            Log.e(TAG,e.getMessage());
        }

        sendPdu(pdu);
    }

    public void sendPdu(ApplicationLayerPdu pdu){

        if(pdu != null){
            timeSinceLastMessage = System.currentTimeMillis();
            mBusy = true;
            sendPduToLowerLayer(pdu);
        }
    }

    public AlMessage convertPduToAlMessage(ThreadPdu pdu){
        AlMessage message = null;

        byte threadCreatorId = pdu.getThreadCreatorId();
        byte threadId =  pdu.getThreadId();
        byte subThreadCreatorId = pdu.getSubThreadCreatorId();
        byte subThreadId = pdu.getSubThreadId();
        byte[] data = pdu.getData();

        switch(pdu.getType()){
            case QUESTION:
                message = new AlQuestion(threadCreatorId, threadId, data);
                break;
            case ANSWER:
                message = new AlAnswer(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);
                break;
            case QUESTION_VOTE:
                message = AlVote.getQuestionVote(threadCreatorId, threadId, data);
                break;
            case ANSWER_VOTE:
                message = AlVote.getAnswerVote(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);
                break;
        }

        return message;
    }

    public AlMessage convertPduToAlMessage(ApplicationLayerPdu pdu){

        switch(pdu.getType()){
            case QUESTION:
            case ANSWER:
            case QUESTION_VOTE:
            case ANSWER_VOTE:
                return convertPduToAlMessage((ThreadPdu) pdu);
        }
        return null;
    }

    public void receiveThreadPdu(ThreadPdu pdu){

        // convert PDU to appropriate Al Message Type
        AlMessage alMessage =  convertPduToAlMessage(pdu);

        AlQuestion retrievedQuestion;
        AlAnswer retrievedAnswer;

        switch(pdu.getType()){
            case QUESTION:
                mSessionThreads.put(
                        (AlQuestion) alMessage,
                        new ArrayList<AlAnswer>()
                );

                break;
            case ANSWER:

                AlAnswer alAnswer = (AlAnswer) alMessage;
                retrievedQuestion = getAlQuestion(
                        alAnswer.getCreatorId(), alAnswer.getQuestionId()
                );

                if(retrievedQuestion != null){
                    mSessionThreads.get(retrievedQuestion).add(alAnswer);
                }
                else{
                    throw new IllegalArgumentException("No question exists for such an answer");
                }

                break;
            case QUESTION_VOTE:

                AlVote questionVote = (AlVote) alMessage;
                retrievedQuestion = getAlQuestion(
                        questionVote.getCreatorId(), questionVote.getQuestionId()
                );
                if(retrievedQuestion != null){
                    retrievedQuestion.addVote(questionVote);
                }
                else{
                    throw new IllegalArgumentException("No question exists for such a vote");
                }
                break;
            case ANSWER_VOTE:

                AlVote answerVote = (AlVote) alMessage;
                retrievedQuestion = getAlQuestion(
                        answerVote.getCreatorId(), answerVote.getQuestionId()
                );
                if(retrievedQuestion != null){
                    retrievedAnswer = getAlAnswer(
                            mSessionThreads.get(retrievedQuestion),
                            answerVote.getAnswerCreatorId(),
                            answerVote.getAnswerId()
                    );

                    if(retrievedAnswer != null){
                        retrievedAnswer.addVote(answerVote);
                    }
                    else{
                        throw new IllegalArgumentException("No answer exists for such a vote");
                    }

                }
                else{
                    throw new IllegalArgumentException("No question exists for such a vote");
                }
                break;
            default:
                throw new IllegalArgumentException("No such type of Thread packet exists");
        }

        mCallback.sendUpperLayer(pdu.getType(), alMessage);
    }

    public void receivePdu(ApplicationLayerPdu pdu){

        switch(pdu.getType()){
            case QUESTION:
            case ANSWER:
            case QUESTION_VOTE:
            case ANSWER_VOTE:
                receiveThreadPdu((ThreadPdu) pdu);
        }
    }

    private void sendPduToLowerLayer(ApplicationLayerPdu pdu) {
        mCurrentPdu = pdu;
        mCallback.transmitPdu(pdu);
    }

}
