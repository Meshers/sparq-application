package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;

/**
 * Created by sarahcs on 4/2/2017.
 */

public class AlPollQuestion extends AlMessage {

    private byte mPollId;
    private byte mQeuestionCreatorId;
    private byte mQuestionId;
    private byte mQuestionFormat;
    private byte[] mQuestionData;
    boolean mHasMore;
    boolean mEndOfPoll;
    boolean mIsMainQuestion;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlPollQuestion(byte pollId, byte questionCreatorId, byte questionId, byte questionFormat,
                          boolean hasMore, boolean endOfPoll, boolean isMainQuestion, byte[] data){
        super(ApplicationLayerPdu.TYPE.POLL_QUESTION);
        this.mPollId = pollId;
        this.mQeuestionCreatorId = questionCreatorId;
        this.mQuestionId = questionId;
        this.mQuestionFormat = questionFormat;
        this.mHasMore = hasMore;
        this.mEndOfPoll = endOfPoll;
        this.mIsMainQuestion = isMainQuestion;
        this.mQuestionData = data;
    }

    public boolean isMainQuestion() {
        return mIsMainQuestion;
    }

    public boolean isEndOfPoll() {
        return mEndOfPoll;
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public byte[] getQuestionData() {
        return mQuestionData;
    }

    public byte getQuestionFormat(){
        return mQuestionFormat;
    }

    public byte[] getCombinedQuestionId(){
        return new byte[]{mQeuestionCreatorId, mQuestionId };
    }

    public byte getQuestionId() {
        return mQuestionId;
    }

    public byte getQeuestionCreatorId() {
        return mQeuestionCreatorId;
    }

    public byte getPollId() {
        return mPollId;
    }

}
