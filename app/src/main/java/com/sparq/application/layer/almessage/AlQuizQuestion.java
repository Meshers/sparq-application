package com.sparq.application.layer.almessage;

import android.util.Log;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by sarahcs on 4/9/2017.
 */

public class AlQuizQuestion extends AlMessage {

    private byte mQuizId;
    private byte mQuestionFormat;
    private byte mNumberOfQuestions;
    private byte[] mQuestionData;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlQuizQuestion(byte quizId,  byte questionFormat, byte numberOfQuestions,
                          byte[] data){
        super(ApplicationLayerPdu.TYPE.POLL_QUESTION);

        this.mQuizId = quizId;
        this.mQuestionFormat = questionFormat;
        this.mNumberOfQuestions = numberOfQuestions;
        this.mQuestionData = data;
    }

    public byte[] getQuestionData() {
        return mQuestionData;
    }

    public String getQuestionDataAsString(){
        return new String(mQuestionData, CHARSET);
    }

    public byte getNumberOfQuestions(){
        return mNumberOfQuestions;
    }

    public byte getQuestionFormat(){
        return mQuestionFormat;
    }

    public byte getQuizId() {
        return mQuizId;
    }

    public static int getQuestionIdFromNumber(int questionNumber){
        return questionNumber - 1;
    }

    public static int getQuestionNumberFromId(int questionId){
        return questionId + 1;
    }

    public int[] getOptionsForQuestion(int questionNumber){

        int questionId = getQuestionIdFromNumber(questionNumber);
        byte numberOfOptions = mQuestionData[questionId];

        int number = Integer.parseInt(new String(new byte[]{numberOfOptions}, CHARSET));


        int[] options = new int[number];
        for(int i = 0; i < number; i++){
            options[i] = (byte) (i+1);
        }

        return options;
    }

    public ArrayList<String> getOptionsForQuestionAsString(int questionNumber){

        int[] optionInt = getOptionsForQuestion(questionNumber);

        ArrayList<String> options = new ArrayList<>();
        for(int i = 0; i < optionInt.length; i++){
            options.add(String.valueOf(optionInt[i]));
        }

        return options;
    }

}
