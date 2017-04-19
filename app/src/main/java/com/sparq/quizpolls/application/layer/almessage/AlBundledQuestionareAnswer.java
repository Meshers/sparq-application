package com.sparq.quizpolls.application.layer.almessage;

import android.util.Log;

import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.quizpolls.util.Constants;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.sparq.quizpolls.util.Constants.CONSTANT_DELIMITER;

/**
 * Created by sarahcs on 4/9/2017.
 */

public class AlBundledQuestionareAnswer extends AlMessage {

    private byte mQuestionareId;
    private byte mQuestionFormat;
    private byte mNumberOfQuestions;
    private byte mAnswerCreatorId;
    private byte[] mData;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlBundledQuestionareAnswer(byte quizId, byte questionFormat, byte numberOfQuestions, byte answerCreatorId,
                                      byte[] data){
        super(ApplicationLayerPdu.TYPE.POLL_QUESTION);

        this.mQuestionareId = quizId;
        this.mQuestionFormat = questionFormat;
        this.mNumberOfQuestions = numberOfQuestions;
        this.mAnswerCreatorId = answerCreatorId;
        this.mData = data;
    }

    public byte[] getAnswerData() {
        return mData;
    }

    public String getAnswerDataAsString() {
        return new String(mData, CHARSET);
    }

    public String getQuestionDataAsString(){
        return new String(mData, CHARSET);
    }

    public byte getAnswerCreatorId(){
        return mAnswerCreatorId;
    }

    public ArrayList<ArrayList<Byte>> getChoices(){

        ArrayList<ArrayList<Byte>> choices = new ArrayList<>(0);

        int i = 0;
        int j = 0;
        while(i < mData.length){
            ArrayList<Byte> choicePerQuestion = new ArrayList<>(0);
            j = i;
            while(mData[j] != CONSTANT_DELIMITER){
                choicePerQuestion.add(mData[j]);
                j++;
            }
            choicePerQuestion.add(mData[j]);

            choices.add(choicePerQuestion);
            i = j+1;
        }

        return choices;
    }

    public String getAnswerChoicesAsString(int questionNumber){
        int questionId = AlBundledQuestionareQuestion.getQuestionIdFromNumber(questionNumber);

        ArrayList<ArrayList<Byte>> choices = getChoices();
        String choiceStr = "";
        for(byte choice: choices.get(questionId)){
            choiceStr += new String(new byte[]{choice}, CHARSET);
        }

        Log.i("HERE", choiceStr.toString());

        // checks if the number of options for a given bundled answer are more than the specific limit
        if(choiceStr.length() <= Constants.MAX_NUMBER_OF_OPTIONS){
            return choiceStr;
        }

        return null;
    }

    public byte getNumberOfQuestions(){
        return mNumberOfQuestions;
    }

    public byte getQuestionFormat(){
        return mQuestionFormat;
    }

    public byte getQuestionareId() {
        return mQuestionareId;
    }

}
