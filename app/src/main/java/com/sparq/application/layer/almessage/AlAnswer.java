package com.sparq.application.layer.almessage;

import java.nio.charset.Charset;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlAnswer {
    private byte mAnswerId;
    private byte mQuestionId;
    private byte mQuestionCreatorId;
    private byte[] mAnswerData;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlAnswer(byte answerId, byte questionId, byte questionCreatorId, byte[] answerData) {
        mAnswerId = answerId;
        mQuestionId = questionId;
        mQuestionCreatorId = questionCreatorId;
        mAnswerData = answerData;
    }

    public byte getAnswerId(){
        return mAnswerId;
    }

    public byte getQuestionId(){
        return mQuestionId;
    }

    public byte getQuestionCreatorId(){
        return mQuestionCreatorId;
    }

    //We should probably have an combined identifier for the question itself
    //Sarah FIXME: 20/3/17
    public byte[] getQuestionIdentifier(){
        return (new byte[]{mQuestionId, mQuestionCreatorId});
    }

    public byte[] getAnswerData() {
        return mAnswerData;
    }

    public String getAnswerDataAsString() {
        return new String(mAnswerData, CHARSET);
    }
}