package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;

/**
 * Created by sarahcs on 4/2/2017.
 */

public class AlPollAnswer extends AlMessage{

    private byte mPollId;
    private byte mQuestionCreatorId;
    private byte mQuestionId;
    private byte mAnswerCreatorId;
    private byte mFormat;
    private byte[] mAnswerData;


    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlPollAnswer(byte pollId, byte questionCreatorId, byte questionId, byte format,byte answerCreatorId, byte[] data) {
        super(ApplicationLayerPdu.TYPE.POLL_ANSWER);
        mQuestionCreatorId = questionCreatorId;
        mQuestionId = questionId;
        mFormat = format;
        mAnswerData = data;

    }

    public byte getPollId() {
        return mPollId;
    }

    public byte getCreatorId(){
        return mQuestionCreatorId;
    }

    public byte getQuestionId(){
        return mQuestionId;
    }

    public byte getFormat(){
        return mFormat;
    }

    public byte getAnswerCreatorId(){
        return mAnswerCreatorId;
    }

    public byte[] getAnswerData() {
        return mAnswerData;
    }

    public String getAnswerDataAsString() {
        return new String(mAnswerData, CHARSET);
    }

}
