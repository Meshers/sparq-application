package com.sparq.application.layer.almessage;

import java.nio.charset.Charset;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlQuestion {
    private byte mQuestionId;
    private byte[] mQuestionData;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlQuestion(byte questionId, byte[] data){
        mQuestionId = questionId;
        mQuestionData = data;
    }

    public byte getQuestionId(){
        return mQuestionId;
    }

    public byte[] getData() {
        return mQuestionData;
    }

    public String getDataAsString() {
        return new String(mQuestionData, CHARSET);
    }
}