package com.sparq.application.layer.pdu;

import test.com.blootoothtester.network.linklayer.LlMessage;

/**
 * Created by sarahcs on 4/9/2017.
 */

public class WifiBTQuizPdu extends ApplicationLayerPdu{

    private final static int QUIZ_ID_BYTES = 1;
    private final static int QUESTION_FORMAT_BYTES = 1;
    private final static int QUESTION_CREATOR_ID_BYTES = 1;
    public final static int ANSWER_CREATOR_ID_BYTES = 1;
    public final static int NUMBER_OF_QUESTIONS_BYTES = 1;

    private final static int PDU_QUIZ_QUESTION_HEADER_BYTES = TYPE_BYTES + QUIZ_ID_BYTES + QUESTION_CREATOR_ID_BYTES + QUESTION_FORMAT_BYTES + NUMBER_OF_QUESTIONS_BYTES;
    private final static int PAYLOAD_QUIZ_QUESTION_BYTES = TOT_SIZE - PDU_QUIZ_QUESTION_HEADER_BYTES;


    public final static int PDU_QUIZ_ANSWER_HEADER_BYTES = TYPE_BYTES + QUIZ_ID_BYTES + QUESTION_CREATOR_ID_BYTES + QUESTION_FORMAT_BYTES + NUMBER_OF_QUESTIONS_BYTES + ANSWER_CREATOR_ID_BYTES;
    private final static int PAYLOAD_QUIZ_ANSWER_BYTES = TOT_SIZE - PDU_QUIZ_ANSWER_HEADER_BYTES;

    private final static int PAYLOAD_QUIZ_MAX_BYTES = Math.max(
            PAYLOAD_QUIZ_QUESTION_BYTES, PDU_QUIZ_ANSWER_HEADER_BYTES
    );

    public final static int HEADER_QUIZ_MAX_BYTES = Math.max(
            PAYLOAD_QUIZ_QUESTION_BYTES, PAYLOAD_QUIZ_ANSWER_BYTES
    );

    private TYPE mType;
    private byte mQuizId;
    private byte mCreatorId;
    private byte mQuestionFormat;
    private byte mNumberOfQuestions;
    private byte mAnswerCreatorId;
    private byte[] mData;

    private WifiBTQuizPdu(TYPE type, byte quizId, byte creatorId, byte questionFormat, byte numberOfQuestions, byte answerCreatorId, byte[] data) {
        super(type);

        this.mType = type;
        this.mQuizId = quizId;
        this.mCreatorId = creatorId;
        this.mQuestionFormat = questionFormat;
        this.mAnswerCreatorId = answerCreatorId;
        this.mNumberOfQuestions = numberOfQuestions;
        this.mData = data;

        if (mData.length > PAYLOAD_QUIZ_MAX_BYTES) {
            throw new IllegalArgumentException("Payload size greater than max (received "
                    + data.length + " max " + PAYLOAD_QUIZ_MAX_BYTES + " bytes)");
        }

        this.mData = data;
    }

    public byte getQuizId() {
        return mQuizId;
    }

    public byte getQuizCreatorId() {
        return mCreatorId;
    }

    public byte getQuestionFormat() {
        return mQuestionFormat;
    }

    public byte getAnswerCreatorId() {
        return mAnswerCreatorId;
    }

    public byte getNumberOfQuestions() {
        return mNumberOfQuestions;
    }

    public byte[] getData() {
        return mData;
    }

    public String getContent(){
        return new String (mData, CHARSET);
    }

    public String getAsString() {
        return new String(encode(), CHARSET);
    }

    @Override
    public byte[] encode(){

        int headerSize = 0;

        switch (getType()) {
            case POLL_QUESTION:
                headerSize = PDU_QUIZ_QUESTION_HEADER_BYTES;
                break;
            case POLL_ANSWER:
                headerSize = PDU_QUIZ_ANSWER_HEADER_BYTES;
                break;
        }
        byte[] encoded = new byte[headerSize + mData.length];

        int nextFieldIndex = 0;

        // add Type
        encoded[nextFieldIndex] = getTypeEncoded(mType);
        nextFieldIndex += TYPE_BYTES;

        //add quiz id
        encoded[nextFieldIndex] = getQuizId();
        nextFieldIndex += QUIZ_ID_BYTES;

        //add question creator Id
        encoded[nextFieldIndex] = getQuizCreatorId();
        nextFieldIndex += QUESTION_CREATOR_ID_BYTES;

        //add the question format
        encoded[nextFieldIndex] = getQuestionFormat();
        nextFieldIndex += QUESTION_FORMAT_BYTES;

        //add number of questions
        encoded[nextFieldIndex] = getNumberOfQuestions();
        nextFieldIndex += NUMBER_OF_QUESTIONS_BYTES;

        switch(getType()){
            case POLL_QUESTION:
                break;
            case POLL_ANSWER:
                encoded[nextFieldIndex] = getAnswerCreatorId();
                nextFieldIndex += ANSWER_CREATOR_ID_BYTES;
                break;
        }

        // add the actual data to send
        System.arraycopy(mData, 0, encoded, nextFieldIndex, mData.length);
        return encoded;
    }

    public static WifiBTQuizPdu decode(byte[] encoded) {

        int nextFieldIndex = 0;
        byte quizId = (byte) 1;
        byte questionCreatorId = (byte) 1;
        byte questionFormat = (byte) 1;
        byte numberOfQuestion = (byte) 1;
        byte answerCreatorId = (byte) 0;

        byte[][] options = null;

        // get type
        TYPE type = getTypeDecoded(encoded[nextFieldIndex]);


        if(type != null){
            nextFieldIndex += TYPE_BYTES;

            //get poll id
            quizId = encoded[nextFieldIndex];
            nextFieldIndex += QUIZ_ID_BYTES;

            //get question creator id
            questionCreatorId = encoded[nextFieldIndex];
            nextFieldIndex += QUESTION_CREATOR_ID_BYTES;

            // get format
            questionFormat = encoded[nextFieldIndex];
            nextFieldIndex += QUESTION_FORMAT_BYTES;

            // get format
            numberOfQuestion = encoded[nextFieldIndex];
            nextFieldIndex += NUMBER_OF_QUESTIONS_BYTES;

            switch(type){
                case POLL_QUESTION:
                    break;
                case POLL_ANSWER:
                    answerCreatorId = encoded[nextFieldIndex];
                    nextFieldIndex += ANSWER_CREATOR_ID_BYTES;
                    break;
            }

            // get the actual data
            byte[] data = new byte[encoded.length - nextFieldIndex];
            System.arraycopy(encoded, nextFieldIndex, data, 0, data.length);

            return new WifiBTQuizPdu(type, quizId, questionCreatorId, questionFormat,numberOfQuestion, answerCreatorId, data);
        }

        return null;
    }

    public static WifiBTQuizPdu getQuestionPdu(byte quizId, byte questionCreatorId, byte questionFormat, byte numberOfQuestions, byte[] data){
        return new WifiBTQuizPdu(TYPE.QUIZ_QUESTION, quizId, questionCreatorId, questionFormat, numberOfQuestions, (byte) 0, data);

    }

    public static WifiBTQuizPdu getAnswerPdu(byte quizId, byte questionCreatorId, byte questionFormat, byte numberOfQuestions, byte answerCreatorId, byte[] data){
        return new WifiBTQuizPdu(TYPE.QUIZ_ANSWER, quizId, questionCreatorId, questionFormat, numberOfQuestions, answerCreatorId, data);
    }

    public static ApplicationLayerPdu from(LlMessage llmessage) {
        return decode(llmessage.getData());

    }


}
