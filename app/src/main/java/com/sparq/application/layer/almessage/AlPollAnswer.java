package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.util.Constants;

import java.nio.charset.Charset;
import java.util.ArrayList;

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
        mPollId = pollId;
        mQuestionCreatorId = questionCreatorId;
        mQuestionId = questionId;
        mFormat = format;
        mAnswerCreatorId = answerCreatorId;
        mAnswerData = data;

    }

    public byte getPollId() {
        return mPollId;
    }

    public byte getQuestionCreatorId(){
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

    public ArrayList<Integer> getAnswerChoicesAsInt(){

        switch(QuestionItem.getFormatFromByte(mFormat)){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                if(mAnswerData.length <= Constants.MAX_NUMBER_OF_OPTIONS){
                    return getAnswerChoicesAsInt(mAnswerData);
                }
        }

        return null;
    }

    public String getAnswerChoicesAsString(){

        switch(QuestionItem.getFormatFromByte(mFormat)){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                if(mAnswerData.length <= Constants.MAX_NUMBER_OF_OPTIONS){
                    return new String(mAnswerData, CHARSET);
                }
        }

        return null;
    }


    public static ArrayList<Integer> getAnswerChoicesAsInt(byte[] choices){
        ArrayList<Integer> intChoices = new ArrayList<Integer>();

        for(int i = 0; i < choices.length; i++){
            intChoices.add((int) choices[i]);
        }

        return intChoices;
    }

    public static ArrayList<String> getAnswerChoicesAsString(byte[] choices){
        ArrayList<String> strChoices = new ArrayList<String>();

        for(int i = 0; i < choices.length; i++){
            strChoices.add((
                    new String(
                            new byte[]{choices[i]},CHARSET
                    )
            ));
        }

        return strChoices;
    }
}
