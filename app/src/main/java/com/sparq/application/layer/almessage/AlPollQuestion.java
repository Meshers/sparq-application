package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by sarahcs on 4/2/2017.
 */

public class AlPollQuestion extends AlMessage {

    private byte mPollId;
    private byte mQeuestionCreatorId;
    private byte mQuestionId;
    private byte mQuestionFormat;
    private byte[] mQuestionData;
    private byte[][] mOptions;
    boolean mHasMore;
    boolean mEndOfPoll;
    boolean mIsMainQuestion;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlPollQuestion(byte pollId, byte questionCreatorId, byte questionId, byte questionFormat,
                          boolean hasMore, boolean endOfPoll, boolean isMainQuestion, byte[] data, byte[][] options){
        super(ApplicationLayerPdu.TYPE.POLL_QUESTION);

        this.mPollId = pollId;
        this.mQeuestionCreatorId = questionCreatorId;
        this.mQuestionId = questionId;
        this.mQuestionFormat = questionFormat;
        this.mHasMore = hasMore;
        this.mEndOfPoll = endOfPoll;
        this.mIsMainQuestion = isMainQuestion;
        this.mQuestionData = data;
        this.mOptions = options;
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

    public String getQuestionDataAsString(){
        return new String(mQuestionData, CHARSET);
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

    public byte[][] getOptions(){
        return mOptions;
    }

    public ArrayList<String> getOptionsAsArray(){
        ArrayList<String> options = null;

        if(mOptions != null){
            options  = new ArrayList<>();
            for(int i = 0; i < mOptions.length; i++){
                options.add(new String (mOptions[i], CHARSET));
            }
        }
        return options;
    }

}
