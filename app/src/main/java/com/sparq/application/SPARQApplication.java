package com.sparq.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.almessage.AlPollAnswer;
import com.sparq.application.layer.almessage.AlPollQuestion;
import com.sparq.application.layer.almessage.AlQuestion;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.NotifyUIHandler;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.ConversationThread;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.UserItem;
import com.sparq.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

import static java.util.Arrays.asList;

/**
 * Created by sarahcs on 3/26/2017.
 */

public class SPARQApplication extends MultiDexApplication {

    private static final String TAG = "SPARQApplication";
    public static SPARQApplication SPARQInstance;

    static MyBluetoothAdapter mBluetoothAdapter;
    static ApplicationPacketDiscoveryHandler mHandler;
    static ApplicationLayerManager mManager;

    private static byte mSessionId = (byte) 1;
    private static byte mOwnAddr = (byte) 1;
    private static final byte mBroadcastId = test.com.blootoothtester.util.Constants.PDU_BROADCAST_ADDR;

    private static int currentQuestionId = 1;
    private static int currentAnswerId = 1;

    public enum USER_TYPE{
        TEACHER,
        STUDENT
    }
    private static USER_TYPE userType;
    private static boolean isTimerElapsed = true;

    //handlers
    static NotifyUIHandler uihandler;

    //timers
    private static CountDownTimer uiTimer;


    private static ArrayList<PollItem> polls;
    private static ArrayList<ConversationThread> conversationThreads;

    @Override
    public void onCreate() {
        super.onCreate();
        // Setup singleton instance
        SPARQInstance = this;
    }

    // Getter to access Singleton instance
    public static SPARQApplication getInstance() {
        return SPARQInstance ;
    }

    public static byte getOwnAddress(){
        return mOwnAddr;
    }

    public static void setOwnAddr(byte ownAddr){
        mOwnAddr = ownAddr;
    }

    public static byte getSessionId(){
        return mSessionId;
    }

    public static void setSessionId(byte sessionId){
        mSessionId = sessionId;
    }

    public static byte getBdcastAddress(){
        return mBroadcastId;
    }

    public static int getCurrentAnswerId() {
        return currentAnswerId;
    }

    public static int getCurrentQuestionId() {
        return currentQuestionId;
    }

    public static USER_TYPE getUserType() {
        return userType;
    }

    public static void setUserType(USER_TYPE userType) {
        SPARQApplication.userType = userType;
    }

    public static boolean isTimerElapsed(){
        return isTimerElapsed;
    }

    public static void setIsTimerElapsed(boolean isTimerElapsed){
        SPARQApplication.isTimerElapsed = isTimerElapsed;
    }


    public void initializeObjects(final Activity activity){

        conversationThreads = new ArrayList<>(0);
        polls = new ArrayList<>(0);

         uiTimer = new CountDownTimer(com.sparq.util.Constants.MAX_TIME_BETWEEN_SEND,
                com.sparq.util.Constants.MAX_TIME_BETWEEN_SEND) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent();
                intent.setAction(Constants.UI_ENABLE_BROADCAST_INTENT);
                getApplicationContext().sendBroadcast(intent);
                isTimerElapsed = true;
                Log.i(TAG, "Timer finished");
            }
        };

    }

    public void startTimer(){

        Intent intent = new Intent();
        intent.setAction(Constants.UI_DISABLE_BROADCAST_INTENT);
        getApplicationContext().sendBroadcast(intent);

        isTimerElapsed = false;
        getInstance().uiTimer.start();
        Log.i(TAG, "Timer started");
    }

    public static void setUINotifier(NotifyUIHandler handler){
        uihandler = handler;
    }

    public static ConversationThread getConversationThread(int questionId, int creatorId){

        for(ConversationThread thread: conversationThreads){
            if(thread.getQuestionareId() == questionId
                    && thread.getCreator().getUserId() == creatorId){
                return thread;
            }
        }

        return null;
    }

    public static PollItem getPoll(int pollId){

        for(PollItem poll: polls){
            if(poll.getQuestionareId() == pollId){
                return poll;
            }
        }

        return null;
    }

    public static void notifyConversationThread(){

        if(uihandler != null){
            uihandler.handleConversationThreadQuestions();
            uihandler.handleConversationThreadAnswers();
            uihandler.handleConversationThreadAnswerVotes();
        }
    }

    public static void handlePackets(ApplicationLayerPdu.TYPE type, AlMessage alMessage){

        PollItem poll;

        switch(type){
            case POLL_QUESTION:

                AlPollQuestion alPollQuestion = (AlPollQuestion) alMessage;
                Log.i(TAG,"RECEIVED MESSAGE: "
                        + alPollQuestion.getPollId() + ":"
                        + alPollQuestion.getQeuestionCreatorId() + ":"
                        + alPollQuestion.getQuestionId() + ":"
                        + alPollQuestion.getQuestionFormat()
                );

                //add to poll or create poll
                poll = getPoll(alPollQuestion.getPollId());

                if(poll == null){
                    poll = new PollItem(
                            alPollQuestion.getPollId(), getSessionId(), null, null,
                            new Date(),
                            PollItem.POLL_STATE.PLAY,
                            new UserItem(alPollQuestion.getQeuestionCreatorId())
                    );

                    polls.add(poll);
                }

                QuestionItem question = null;
                switch(QuestionItem.getFormatFromByte(alPollQuestion.getQuestionFormat())){
                    case MCQ_SINGLE:
                        question = QuestionItem.getMCQSingleQuestion(
                                alPollQuestion.getQuestionId(),
                                alPollQuestion.getPollId(),
                                alPollQuestion.getQuestionDataAsString(),
                                alPollQuestion.getOptionsAsArray(),
                                Constants.MIN_QUESTION_MARKS);
                        break;
                    case MCQ_MULTIPLE:
                        question = QuestionItem.getMCQMultipleQuestion(
                                alPollQuestion.getQuestionId(),
                                alPollQuestion.getPollId(),
                                alPollQuestion.getQuestionDataAsString(),
                                alPollQuestion.getOptionsAsArray(),
                                Constants.MIN_QUESTION_MARKS
                        );
                        break;
                    case ONE_WORD:
                        question = QuestionItem.getOneWordQuestion(alPollQuestion.getQuestionId(),
                                alPollQuestion.getPollId(),
                                alPollQuestion.getQuestionDataAsString(),
                                Constants.MIN_QUESTION_MARKS);
                        break;
                    case SHORT:
                        question = QuestionItem.getShortQuestion(alPollQuestion.getQuestionId(),
                                alPollQuestion.getPollId(),
                                alPollQuestion.getQuestionDataAsString(),
                                Constants.MIN_QUESTION_MARKS);
                        break;
                }

                poll.addQuestionToList(alPollQuestion.getQuestionId(), question);

                if(alPollQuestion.isMainQuestion()){
                    poll.setName(alPollQuestion.getQuestionDataAsString());
                }

                break;
            case POLL_ANSWER:

                AlPollAnswer alPollAnswer = (AlPollAnswer) alMessage;
                Log.i(TAG,"RECEIVED MESSAGE: "
                        + alPollAnswer.getPollId() + ":"
                        + alPollAnswer.getQuestionCreatorId() + ":"
                        + alPollAnswer.getQuestionId() + ":"
                        + alPollAnswer.getAnswerCreatorId() + ":"
                        + alPollAnswer.getFormat()
                );

                poll = getPoll(alPollAnswer.getPollId());

                if(poll == null){
                    return;
                }

                AnswerItem pollAnswer = null;
                switch(QuestionItem.getFormatFromByte(alPollAnswer.getFormat())){
                    case MCQ_SINGLE:
                        pollAnswer = AnswerItem.getMCQSingleAnswer(
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getQuestionCreatorId()),
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getAnswerCreatorId()),
                                alPollAnswer.getAnswerChoicesAsString()
                        );

                        break;
                    case MCQ_MULTIPLE:
                        pollAnswer = AnswerItem.getMCQMultipleAnswer(
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getQuestionCreatorId()),
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getAnswerCreatorId()),
                                alPollAnswer.getAnswerChoicesAsString()
                        );

                        break;
                    case ONE_WORD:
                        pollAnswer = AnswerItem.getMCQOneWordAnswer(
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getQuestionCreatorId()),
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getAnswerCreatorId()),
                                alPollAnswer.getAnswerDataAsString()
                        );
                        break;
                    case SHORT:
                        pollAnswer = AnswerItem.getShortAnswer(
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getQuestionCreatorId()),
                                alPollAnswer.getQuestionId(),
                                new UserItem(alPollAnswer.getAnswerCreatorId()),
                                alPollAnswer.getAnswerDataAsString()
                        );
                        break;
                }

                poll.addAnswerToQuestion(alPollAnswer.getQuestionId(), pollAnswer);

                break;
            case QUESTION:

                AlQuestion alQuestion = (AlQuestion) alMessage;
                Log.i(TAG,"RECEIVED MESSAGE: "
                        + alQuestion.getCreatorId() + ":" +alQuestion.getQuestionId() + ":" + alQuestion.getDataAsString());

                // create a conversation thread
                ConversationThread newThread = new ConversationThread(
                        alQuestion.getQuestionId(),
                        getSessionId(),
                        new Date(),
                        new UserItem(alQuestion.getCreatorId()),
                        alQuestion.getDataAsString()
                );

                conversationThreads.add(newThread);

                notifyConversationThread();

                break;
            case ANSWER:

                AlAnswer alAnswer = (AlAnswer) alMessage;
                Log.i(TAG, "RECEIVED MESSAGE: "
                        + alAnswer.getCreatorId() + ":"
                        + alAnswer.getQuestionId()+ ":"
                        + alAnswer.getAnswerCreatorId()+ ":"
                        + alAnswer.getAnswerId()+ ":"
                        + alAnswer.getAnswerDataAsString()
                );

                AnswerItem newAnswer = AnswerItem.getAnswerItemFromMessage(alAnswer);

                getConversationThread(
                        alAnswer.getQuestionId(), alAnswer.getCreatorId()
                ).addAnswerToList(newAnswer);

                notifyConversationThread();

                break;
            case QUESTION_VOTE:

                AlVote alQuestionVote = (AlVote) alMessage;
                Log.i(TAG, "RECEIVED MESSAGE: "
                        + alQuestionVote.getCreatorId() + ":"
                        + alQuestionVote.getQuestionId() +":"
                        + alQuestionVote.getVoteValue()
                );

                ConversationThread thread = getConversationThread(alQuestionVote.getQuestionId(), alQuestionVote.getCreatorId());

                if(thread != null){
                    switch(alQuestionVote.getVoteValue()){
                        case UPVOTE:
                            thread.getQuestionItem().addUpVote();
                            break;
                        case DOWNVOTE:
                            thread.getQuestionItem().addDownVote();
                            break;
                    }
                }

                notifyConversationThread();

                break;
            case ANSWER_VOTE:

                AlVote alAnswerVote = (AlVote) alMessage;
                Log.i(TAG, "RECEIVED MESSAGE: "
                        + alAnswerVote.getCreatorId() + ":"
                        + alAnswerVote.getQuestionId()+ ":"
                        + alAnswerVote.getAnswerCreatorId()+ ":"
                        + alAnswerVote.getAnswerId()+ ":"
                        + alAnswerVote.getVoteValue()
                );

                AnswerItem answer = getConversationThread(
                        alAnswerVote.getQuestionId(), + alAnswerVote.getCreatorId()
                ).getAnswer(alAnswerVote.getAnswerId(), alAnswerVote.getAnswerCreatorId());

                if(answer != null){
                    switch(alAnswerVote.getVoteValue()){
                        case UPVOTE:
                            answer.addUpVote();
                            break;
                        case DOWNVOTE:
                            answer.addDownVote();
                            break;
                    }
                }

                notifyConversationThread();

                break;
            default:
                throw new IllegalArgumentException("Illegal message type.");
        }
    }

    public static void sendPollMessage(ApplicationLayerPdu.TYPE type, byte toAddr, String msg,
                                       int pollId, int creatorId, int questionId, QuestionItem.FORMAT questionFormat,
                                       ArrayList<String> options, int answerCreatorId,
                                       boolean hasMore, boolean endOfPoll, boolean isMainQuestion){

        boolean isSent;

        switch(type){
            case POLL_QUESTION:

                if(QuestionItem.getFormatAsByte(questionFormat) == (byte) 0){
                    isSent = false;
                }else{
                    isSent = mManager.sendData(
                            ApplicationLayerPdu.TYPE.POLL_QUESTION,
                            msg,
                            options,
                            toAddr,
                            asList((byte) pollId, (byte) creatorId, (byte) questionId,
                                    QuestionItem.getFormatAsByte(questionFormat),(byte) 0),
                            asList(hasMore, endOfPoll, isMainQuestion)
                    );
                }

                if(isSent){

                    PollItem poll = getPoll(pollId);

                    if(poll == null){
                        poll = new PollItem(
                                pollId, getSessionId(), null, null,
                                new Date(),
                                PollItem.POLL_STATE.PLAY,
                                new UserItem(creatorId)
                        );

                        polls.add(poll);
                    }

                    QuestionItem question = null;
                    switch(questionFormat){
                        case MCQ_SINGLE:
                            question = QuestionItem.getMCQSingleQuestion(questionId, pollId, msg, options, Constants.MIN_QUESTION_MARKS);
                            break;
                        case MCQ_MULTIPLE:
                            question = QuestionItem.getMCQMultipleQuestion(questionId, pollId, msg, options, Constants.MIN_QUESTION_MARKS);
                            break;
                        case ONE_WORD:
                            question = QuestionItem.getOneWordQuestion(questionId, pollId, msg, Constants.MIN_QUESTION_MARKS);
                            break;
                        case SHORT:
                            question = QuestionItem.getShortQuestion(questionId, pollId, msg, Constants.MIN_QUESTION_MARKS);
                            break;
                    }

                    poll.addQuestionToList(questionId, question);

                    if(isMainQuestion){
                        poll.setName(msg);
                    }

                }
                break;
            case POLL_ANSWER:

                isSent = mManager.sendData(
                        ApplicationLayerPdu.TYPE.POLL_ANSWER,
                        msg,
                        null,
                        toAddr,
                        asList((byte) pollId, (byte) creatorId, (byte) questionId,
                                QuestionItem.getFormatAsByte(questionFormat),(byte) answerCreatorId),
                        asList(false, false, false)
                );

                if(isSent){
                    PollItem poll = getPoll(pollId);

                    if(poll == null){
                        return;
                    }

                    AnswerItem answer = null;
                    switch(questionFormat){
                        case MCQ_SINGLE:
                            answer = AnswerItem.getMCQSingleAnswer(
                                    questionId, new UserItem(answerCreatorId),
                                    questionId, new UserItem(creatorId),
                                    msg);
                            break;
                        case MCQ_MULTIPLE:
                            answer = AnswerItem.getMCQMultipleAnswer(
                                    questionId, new UserItem(answerCreatorId),
                                    questionId, new UserItem(creatorId),
                                    msg);
                            break;
                        case ONE_WORD:
                            answer = AnswerItem.getMCQOneWordAnswer(
                                    questionId, new UserItem(answerCreatorId),
                                    questionId, new UserItem(creatorId),
                                    msg);
                            break;
                        case SHORT:
                            answer = AnswerItem.getShortAnswer(
                                    questionId, new UserItem(answerCreatorId),
                                    questionId, new UserItem(creatorId),
                                    msg);
                            break;
                    }

                    poll.addAnswerToQuestion(questionId, answer);

                }
                break;
        }
    }

    public static void sendThreadMessage(ApplicationLayerPdu.TYPE type, byte toAddr, String msg,
                                         int creatorId, int questionId, int answerCreatotId, int answerId,
                                         AlVote.VOTE_TYPE voteType){

        boolean isSent;

        switch(type){
            case QUESTION:

                // create a conversation thread

                isSent = mManager.sendData(
                        ApplicationLayerPdu.TYPE.QUESTION,
                        msg,
                        null,
                        toAddr,
                        asList((byte) creatorId, (byte) questionId, (byte) 0, (byte) 0),
                        null
                );

                if(isSent){
                    ConversationThread newThread = new ConversationThread(
                            questionId,
                            getSessionId(),
                            new Date(),
                            new UserItem(creatorId),
                            msg
                    );
                    conversationThreads.add(newThread);

                    currentQuestionId ++;

                    getInstance().startTimer();
                    notifyConversationThread();
                }

                break;
            case ANSWER:

                isSent = mManager.sendData(
                        ApplicationLayerPdu.TYPE.ANSWER,
                        msg,
                        null,
                        toAddr,
                        asList((byte) creatorId, (byte) questionId, (byte) answerCreatotId, (byte) answerId),
                        null
                );

                if(isSent){
                    AnswerItem newAnswer = AnswerItem.getShortAnswer(
                            answerId,
                            new UserItem(answerCreatotId),
                            questionId,
                            new UserItem(creatorId),
                            msg
                    );

                    getConversationThread(questionId, creatorId).addAnswerToList(newAnswer);

                    currentAnswerId++;

                    notifyConversationThread();
                }

                break;
            case QUESTION_VOTE:

                isSent = mManager.sendData(
                        ApplicationLayerPdu.TYPE.QUESTION_VOTE,
                        new String(
                                new byte[]{AlVote.getVoteEncoded(voteType)},
                                Constants.CHARSET
                        ),
                        null,
                        toAddr,
                        asList((byte) creatorId, (byte) questionId, (byte) 0, (byte) 0),
                        null
                );

                if(isSent){
                    ConversationThread thread = getConversationThread(questionId, creatorId);

                    if(thread != null){
                        switch(voteType){
                            case UPVOTE:
                                thread.getQuestionItem().addUpVote();
                                break;
                            case DOWNVOTE:
                                thread.getQuestionItem().addDownVote();
                                break;
                        }

                        // set the voted boolean to true
                        thread.getQuestionItem().setHasVoted(true);
                        notifyConversationThread();
                    }



                }

                break;
            case ANSWER_VOTE:

                isSent = mManager.sendData(
                        ApplicationLayerPdu.TYPE.ANSWER_VOTE,
                        new String(
                                new byte[]{AlVote.getVoteEncoded(voteType)},
                                Constants.CHARSET
                        ),
                        null,
                        toAddr,
                        asList((byte) creatorId, (byte) questionId, (byte) answerCreatotId, (byte) answerId),
                        null
                );

                if(isSent){
                    AnswerItem answer = getConversationThread(questionId, creatorId)
                            .getAnswer(answerId, answerCreatotId);

                    if(answer != null){
                        switch(voteType){
                            case UPVOTE:
                                answer.addUpVote();
                                break;
                            case DOWNVOTE:
                                answer.addDownVote();
                                break;
                        }

                        answer.setHasVoted(true);
                        notifyConversationThread();
                    }


                }

                break;
        }

    }


//    public static MyBluetoothAdapter getBluetoothAdapter(){
//        return mBluetoothAdapter;
//    }
//
//    public static ApplicationPacketDiscoveryHandler getHandler(){
//        return mHandler;
//    }
//
    public static void setApplicationLayerManager(ApplicationLayerManager manager){
        mManager = manager;
    }

    public static ArrayList<ConversationThread> getConversationThreads(){
        return conversationThreads;
    }

}
