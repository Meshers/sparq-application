package com.sparq.application.layer;

import android.util.Log;

import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.almessage.AlPollAnswer;
import com.sparq.application.layer.almessage.AlPollQuestion;
import com.sparq.application.layer.almessage.AlQuestion;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.PollPdu;
import com.sparq.application.layer.pdu.ThreadPdu;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sparq.util.Constants.TEACHER_ADDRESS;

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

    // application layer DS
    private HashMap<AlQuestion, ArrayList<AlAnswer>> mSessionThreads;
    private HashMap<Byte, ArrayList<AlPollQuestion>> mSessionPolls;

    public interface Callback {
        void transmitPdu(ApplicationLayerPdu pdu, byte toAddr);

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

    public static AlPollQuestion getAlPollQuestion(ArrayList<AlPollQuestion> questions, byte questionCreatorId, byte questionId){

        for(AlPollQuestion question: questions){

            byte[] combinedQuestionId = question.getCombinedQuestionId();

            if(combinedQuestionId[0] == questionCreatorId && combinedQuestionId[1] == questionId){
                return question;
            }
        }
        return null;
    }

    public AlQuestion addDummyQuestion(byte threadCreatorId, byte threadId){

        AlQuestion alQuestion = new AlQuestion(threadCreatorId, threadId, null, true);

        this.mSessionThreads.put(alQuestion, new ArrayList<AlAnswer>()
        );

        return alQuestion;
    }

    public AlAnswer addDummyAnswer(byte threadCreatorId, byte threadId, byte subThreadCreatorId, byte subThreadId){
        AlAnswer alAnswer = new AlAnswer(threadCreatorId, threadId, subThreadCreatorId, subThreadId, null, true);

        //get the corresponding answer array
        AlQuestion retrievedQuestion = getAlQuestion(threadCreatorId, threadId);

        mSessionThreads.get(retrievedQuestion).add(alAnswer);

        return alAnswer;

    }

    public boolean sendThreadPdu(ApplicationLayerPdu.TYPE type, byte threadCreatorId, byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data, byte toAddr){

        ApplicationLayerPdu pdu = null;
        AlQuestion retrievedQuestion;
        AlAnswer retrievedAnswer;

        try{
            switch(type){
                case QUESTION:

                    AlQuestion question = new AlQuestion(threadCreatorId, threadId, data, false);
                    mSessionThreads.put(question, new ArrayList<AlAnswer>());

                    pdu = ThreadPdu.getQuestionPdu(threadCreatorId, threadId, data);
                    break;
                case ANSWER:

                    AlAnswer answer = new AlAnswer(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data, false);

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
            return false;
        }

        sendPdu(pdu, toAddr);
        return true;
    }

    public boolean sendPollPdu(ApplicationLayerPdu.TYPE type, byte pollId,
                               byte questionCreatorId, byte questionId, byte questionFormat,byte answerCreatorId,
                               boolean hasMore, boolean endOfPoll, boolean isMainQuestion, byte[] data, byte toAddr){

        ApplicationLayerPdu pdu = null;
        AlPollQuestion retreivedQuestion;

        try{
            switch(type){
                case POLL_QUESTION:

                    if(SPARQApplication.getUserType() != SPARQApplication.USER_TYPE.TEACHER){
                        return false;
                    }

                    AlPollQuestion question = new AlPollQuestion(pollId, questionCreatorId, questionId, questionFormat, hasMore, endOfPoll, isMainQuestion, data);

                    // check if it is the first question of a poll if not add it
                    if(!mSessionPolls.keySet().contains(pollId)){
                        mSessionPolls.put(
                                pollId,
                                new ArrayList<AlPollQuestion>(0));
                    }

                    mSessionPolls.get(pollId).add(question);

                    pdu = PollPdu.getQuestionPdu(pollId, questionCreatorId, questionId, questionFormat, hasMore, endOfPoll, isMainQuestion, data);

                    break;
                case POLL_ANSWER:

                    // check if corresponding question exists
                    if(mSessionPolls.keySet().contains(pollId)){
                        retreivedQuestion = getAlPollQuestion(
                                mSessionPolls.get(pollId),
                                questionCreatorId,
                                questionId
                        );

                        if(retreivedQuestion == null){
                            // a question exists for the received answer
                            throw new IllegalArgumentException("No such type of Poll packet exists");
                        }

                        pdu = PollPdu.getAnswerPdu(pollId,questionCreatorId, questionId, answerCreatorId, data);
                    }else{
                        throw new IllegalArgumentException("No such type of Thread packet exists");
                    }

                    break;
            }
        }
        catch(IllegalArgumentException e){
            Log.e(TAG,e.getMessage());
            return false;
        }

        sendPdu(pdu, toAddr);
        return true;


    }


    public void sendPdu(ApplicationLayerPdu pdu, byte toAddr){

        if(pdu != null){
            timeSinceLastMessage = System.currentTimeMillis();
            mBusy = true;
            sendPduToLowerLayer(pdu, toAddr);
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
                message = new AlQuestion(threadCreatorId, threadId, data, false);
                break;
            case ANSWER:
                message = new AlAnswer(threadCreatorId, threadId, subThreadCreatorId, subThreadId, data, false);
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

    public AlMessage convertPduToAlMessage(PollPdu pdu){

        AlMessage message = null;

        byte pollId = pdu.getPollId();
        byte questionCreatorId = pdu.getQuestionCreatorId();
        byte questionId =  pdu.getQuestionId();
        byte questionFormat = pdu.getQuestionFormat();
        byte answerCreatorId = pdu.getAnswerCreatorId();
        boolean hasMore = pdu.hasMore();
        boolean endOfPoll = pdu.endOfPoll();
        boolean isMainQuestion = pdu.isMainQuestion();

        byte[] data = pdu.getData();

        switch(pdu.getType()){
            case POLL_QUESTION:
                message = new AlPollQuestion(pollId, questionCreatorId, questionId, questionFormat, hasMore, endOfPoll, isMainQuestion, data);
                break;
            case POLL_ANSWER:
                message = new AlPollAnswer(pollId, questionCreatorId, questionId, questionFormat,answerCreatorId, data);
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

    public void transmitPacketsToUpperLayer(ApplicationLayerPdu.TYPE type, AlMessage alMessage){

        switch(type){
            case POLL_QUESTION:
                mCallback.sendUpperLayer(type, alMessage);
                break;
            case POLL_ANSWER:
                mCallback.sendUpperLayer(type, alMessage);
                break;
            case QUESTION:
                // transmit the question, its answers, and all vote packets to the upper layer
                mCallback.sendUpperLayer(type, alMessage);

                for(AlAnswer answer: mSessionThreads.get((AlQuestion) alMessage)){
                    if(!answer.isDummy()){
                        transmitPacketsToUpperLayer(ApplicationLayerPdu.TYPE.ANSWER, answer);
                    }

                }

                for(AlVote vote: ((AlQuestion) alMessage).getVotes()){
                    transmitPacketsToUpperLayer(ApplicationLayerPdu.TYPE.QUESTION_VOTE, vote);
                }

                ((AlQuestion) alMessage).setVotes(new ArrayList<AlVote>());

                break;
            case ANSWER:
                // transmit the answer and all its votes to the upper layer
                mCallback.sendUpperLayer(type, alMessage);

                for(AlVote vote: ((AlAnswer) alMessage).getVotes()){
                    transmitPacketsToUpperLayer(ApplicationLayerPdu.TYPE.ANSWER_VOTE, vote);
                }

                ((AlAnswer) alMessage).setVotes(new ArrayList<AlVote>());


                break;
            case QUESTION_VOTE:
            case ANSWER_VOTE:

                mCallback.sendUpperLayer(type, alMessage);

                break;
        }
    }


    public void receiveThreadPdu(ThreadPdu pdu){

        // convert PDU to appropriate Al Message Type
        AlMessage alMessage =  convertPduToAlMessage(pdu);

        AlQuestion retrievedQuestion;
        AlAnswer retrievedAnswer;
        boolean transmitPdu = true;

        switch(pdu.getType()){
            case QUESTION:

                Log.i(TAG, "QUESTION RECEIVED");

                AlQuestion alQuestion = (AlQuestion) alMessage;

                //check if question exists
                retrievedQuestion = getAlQuestion(alQuestion.getCreatorId(), alQuestion.getQuestionId());
                if(retrievedQuestion != null && retrievedQuestion.isDummy()){
                    // a duplicate question exists
                    Log.i(TAG, "Duplicate question");
                    retrievedQuestion.copyData(alQuestion);
                    alQuestion = retrievedQuestion;
                }else{
                    mSessionThreads.put(
                            alQuestion,
                            new ArrayList<AlAnswer>(0)
                    );
                }

                transmitPacketsToUpperLayer(pdu.getType(),alQuestion);


                break;
            case ANSWER:

                Log.i(TAG, "ANSWER RECEIVED");

                AlAnswer alAnswer = (AlAnswer) alMessage;
               // check if question exists
                retrievedQuestion = getAlQuestion(alAnswer.getCreatorId(), alAnswer.getQuestionId());

                if(retrievedQuestion == null){
                    // no such question exists. Add dummy question
                    Log.i(TAG, "Dummy question");
                    retrievedQuestion = addDummyQuestion(alAnswer.getCreatorId(), alAnswer.getQuestionId());
                }

                retrievedAnswer = getAlAnswer(
                        mSessionThreads.get(retrievedQuestion),
                        alAnswer.getAnswerCreatorId(), alAnswer.getAnswerId());

                if(retrievedAnswer != null && retrievedAnswer.isDummy()){
                    // a duplicate answer exists
                    Log.i(TAG, "duplicate ans");
                    retrievedAnswer.copyData(alAnswer);
                    alAnswer = retrievedAnswer;
                }else{
                    mSessionThreads.get(retrievedQuestion).add(alAnswer);
                }

                if(!retrievedQuestion.isDummy()){
                    transmitPacketsToUpperLayer(pdu.getType(),alAnswer);
                }

                break;
            case QUESTION_VOTE:

                Log.i(TAG, "QUESTION VOTE RECEIVED");

                AlVote questionVote = (AlVote) alMessage;

                retrievedQuestion = getAlQuestion(questionVote.getCreatorId(), questionVote.getQuestionId());

                if(retrievedQuestion == null){
                    Log.i(TAG, "Dummy question");
                    // no such question exists. Add dummy question
                    retrievedQuestion = addDummyQuestion(questionVote.getCreatorId(), questionVote.getQuestionId());
                }
                retrievedQuestion.addVote(questionVote);

                if(!retrievedQuestion.isDummy()){
                    transmitPacketsToUpperLayer(pdu.getType(),questionVote);
                }

                break;
            case ANSWER_VOTE:

                Log.i(TAG, "ANSWER VOTE RECEIVED");
                AlVote answerVote = (AlVote) alMessage;

                retrievedQuestion = getAlQuestion(answerVote.getCreatorId(), answerVote.getQuestionId());
                if(retrievedQuestion == null){
                    Log.i(TAG, "Dummy question");
                    retrievedQuestion = addDummyQuestion(answerVote.getCreatorId(), answerVote.getQuestionId());
                }

                retrievedAnswer = getAlAnswer(
                        mSessionThreads.get(retrievedQuestion),
                        answerVote.getAnswerCreatorId(), answerVote.getAnswerId());

                if(retrievedAnswer == null){
                    Log.i(TAG, "Dummy Ans");
                    retrievedAnswer = addDummyAnswer(
                            answerVote.getCreatorId(), answerVote.getQuestionId(),
                            answerVote.getAnswerCreatorId(), answerVote.getAnswerId()
                    );

                }

                retrievedAnswer.addVote(answerVote);

                if(!retrievedQuestion.isDummy() && !retrievedAnswer.isDummy()){
                    transmitPacketsToUpperLayer(pdu.getType(),answerVote);
                }


                break;
            default:
                throw new IllegalArgumentException("No such type of Thread packet exists");
        }
//
//        mCallback.sendUpperLayer(pdu.getType(), alMessage);
    }

    public void receivePollPdu(PollPdu pdu){

        // convert PDU to appropriate Al Message Type
        AlMessage alMessage =  convertPduToAlMessage(pdu);

        AlPollQuestion retreivedQuestion;

        switch(pdu.getType()){
            case POLL_QUESTION:

                Log.i(TAG, "POLL QUESTION RECEIVED");

                AlPollQuestion alPollQuestion = (AlPollQuestion) alMessage;

                // check if question was from teacher
                if(alPollQuestion.getQeuestionCreatorId() != TEACHER_ADDRESS){
                    return;
                }

                // check if it is the first question of a poll if not add it
                if(!mSessionPolls.keySet().contains(pdu.getPollId())){
                    mSessionPolls.put(
                            pdu.getPollId(),
                            new ArrayList<AlPollQuestion>(0));
                }

                mSessionPolls.get(pdu.getPollId()).add(alPollQuestion);

                if(alPollQuestion.isEndOfPoll()){
                    //transmit all the poll questions to the upper layer
                    for(AlPollQuestion question: mSessionPolls.get(pdu.getPollId())){
                        transmitPacketsToUpperLayer(pdu.getType(), question);
                    }
                }

                break;
            case POLL_ANSWER:

                /**
                 * teacher: can receive any number of answers
                 * students can send only one answer per question and cannot receive any answers
                 */

                AlPollAnswer alPollAnswer = (AlPollAnswer) alMessage;

                switch(SPARQApplication.getUserType()){
                    case STUDENT:
                        // check if answer belongs to given user
                        if(alPollAnswer.getAnswerCreatorId() != SPARQApplication.getOwnAddress()){
                            // revieved someone else's answer
                            return;
                        }

                    case TEACHER:

                        if(mSessionPolls.keySet().contains(pdu.getPollId())){
                            retreivedQuestion = getAlPollQuestion(
                                    mSessionPolls.get(pdu.getPollId()),
                                    alPollAnswer.getCreatorId(),
                                    alPollAnswer.getQuestionId()
                            );

                            if(retreivedQuestion != null){
                                // a question exists for the received answer
                                transmitPacketsToUpperLayer(pdu.getType(), alPollAnswer);
                            }else{
                                throw new IllegalArgumentException("No such type of Poll packet exists");
                            }
                        }else{
                            throw new IllegalArgumentException("No such type of Thread packet exists");
                        }

                        break;
                }

                break;
            default:
                throw new IllegalArgumentException("No such type of Poll packet exists");
        }
    }

    public void receivePdu(ApplicationLayerPdu pdu){

        switch(pdu.getType()){
            case POLL_QUESTION:
            case POLL_ANSWER:
                receivePollPdu((PollPdu) pdu);
                break;
            case QUESTION:
            case ANSWER:
            case QUESTION_VOTE:
            case ANSWER_VOTE:
                receiveThreadPdu((ThreadPdu) pdu);
        }
    }

    private void sendPduToLowerLayer(ApplicationLayerPdu pdu, byte toAddr) {
        mCurrentPdu = pdu;
        mCallback.transmitPdu(pdu, toAddr);
    }

}
