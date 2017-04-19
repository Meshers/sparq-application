package com.sparq.quizpolls.application.layer.pdu;

import test.com.blootoothtester.network.linklayer.bt.LlMessage;
import test.com.blootoothtester.network.linklayer.wifi.BtMessage;
import test.com.blootoothtester.network.linklayer.wifi.WifiMessage;

/**
 * Created by sarahcs on 4/9/2017.
 */

public class WifiBTQuestionarePdu extends ApplicationLayerPdu{

    private final static int QUIZ_ID_BYTES = 1;
    private final static int QUESTION_FORMAT_BYTES = 1;
    public final static int ANSWER_CREATOR_ID_BYTES = 1;
    public final static int NUMBER_OF_QUESTIONS_BYTES = 1;

    private final static int PDU_QUESTIONARE_QUESTION_HEADER_BYTES = TYPE_BYTES + QUIZ_ID_BYTES + QUESTION_FORMAT_BYTES + NUMBER_OF_QUESTIONS_BYTES;
    private final static int PAYLOAD_QUESTIONARE_QUESTION_BYTES = TOT_SIZE_WIFI - PDU_QUESTIONARE_QUESTION_HEADER_BYTES;


    public final static int PDU_QUESTIONARE_ANSWER_HEADER_BYTES = TYPE_BYTES + QUIZ_ID_BYTES + QUESTION_FORMAT_BYTES + NUMBER_OF_QUESTIONS_BYTES + ANSWER_CREATOR_ID_BYTES;
    private final static int PAYLOAD_QUESTIONARE_ANSWER_BYTES = TOT_SIZE_BT - PDU_QUESTIONARE_ANSWER_HEADER_BYTES;

    private final static int PAYLOAD_QUIZ_MAX_BYTES = Math.max(
            PAYLOAD_QUESTIONARE_QUESTION_BYTES, PAYLOAD_QUESTIONARE_ANSWER_BYTES
    );

    public final static int HEADER_QUIZ_MAX_BYTES = Math.max(
            PDU_QUESTIONARE_QUESTION_HEADER_BYTES, PDU_QUESTIONARE_ANSWER_HEADER_BYTES
    );

    private byte mQuestionareId;
    private byte mQuestionFormat;
    private byte mNumberOfQuestions;
    private byte mAnswerCreatorId;

    private byte mToAddr;
    private byte mLinkId;

    private byte[] mData;

    private WifiBTQuestionarePdu(TYPE type, byte quizId, byte questionFormat, byte numberOfQuestions, byte answerCreatorId, byte[] data) {
        super(type);

        this.mQuestionareId = quizId;
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

    public byte getQuestionareId() {
        return mQuestionareId;
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

    public byte getToAddr() {
        return mToAddr;
    }

    public void setToAddr(byte mToAddr) {
        this.mToAddr = mToAddr;
    }

    public byte getLinkId() {
        return mLinkId;
    }

    public void setLinkId(byte mLinkId) {
        this.mLinkId = mLinkId;
    }

    @Override
    public byte[] encode(){

        int headerSize = 0;

        switch (getType()) {
            case QUIZ_QUESTION:
                headerSize = PDU_QUESTIONARE_QUESTION_HEADER_BYTES;
                break;
            case QUIZ_ANSWER:
                headerSize = PDU_QUESTIONARE_ANSWER_HEADER_BYTES;
                break;
            case POLL_QUESTION:
                headerSize = PDU_QUESTIONARE_QUESTION_HEADER_BYTES;
                break;
            case POLL_ANSWER:
                headerSize = PDU_QUESTIONARE_ANSWER_HEADER_BYTES;
                break;
        }
        byte[] encoded = new byte[headerSize + mData.length];

        int nextFieldIndex = 0;

        // add Type
        encoded[nextFieldIndex] = getTypeEncoded(getType());
        nextFieldIndex += TYPE_BYTES;

        //add quiz id
        encoded[nextFieldIndex] = getQuestionareId();
        nextFieldIndex += QUIZ_ID_BYTES;

        //add the question format
        encoded[nextFieldIndex] = getQuestionFormat();
        nextFieldIndex += QUESTION_FORMAT_BYTES;

        //add number of questions
        encoded[nextFieldIndex] = getNumberOfQuestions();
        nextFieldIndex += NUMBER_OF_QUESTIONS_BYTES;

        switch(getType()){
            case QUIZ_QUESTION:
            case POLL_QUESTION:
                break;
            case QUIZ_ANSWER:
            case POLL_ANSWER:
                encoded[nextFieldIndex] = getAnswerCreatorId();
                nextFieldIndex += ANSWER_CREATOR_ID_BYTES;
                break;
        }

        // add the actual data to send
        System.arraycopy(mData, 0, encoded, nextFieldIndex, mData.length);
        return encoded;
    }

    public static WifiBTQuestionarePdu decode(byte[] encoded) {

        int nextFieldIndex = 0;
        byte quizId = (byte) 1;
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

            // get format
            questionFormat = encoded[nextFieldIndex];
            nextFieldIndex += QUESTION_FORMAT_BYTES;

            // get format
            numberOfQuestion = encoded[nextFieldIndex];
            nextFieldIndex += NUMBER_OF_QUESTIONS_BYTES;

            switch(type){
                case POLL_QUESTION:
                case QUIZ_QUESTION:
                    break;
                case QUIZ_ANSWER:
                case POLL_ANSWER:
                    answerCreatorId = encoded[nextFieldIndex];
                    nextFieldIndex += ANSWER_CREATOR_ID_BYTES;
                    break;
            }

            // get the actual data
            byte[] data = new byte[encoded.length - nextFieldIndex];
            System.arraycopy(encoded, nextFieldIndex, data, 0, data.length);

            return new WifiBTQuestionarePdu(type, quizId, questionFormat,numberOfQuestion, answerCreatorId, data);
        }

        return null;
    }

    public static WifiBTQuestionarePdu getQuizQuestionPdu(byte quizId, byte questionFormat, byte numberOfQuestions, byte[] data){
        return new WifiBTQuestionarePdu(TYPE.QUIZ_QUESTION, quizId, questionFormat, numberOfQuestions, (byte) 0, data);

    }

    public static WifiBTQuestionarePdu getQuizAnswerPdu(byte quizId, byte questionFormat, byte numberOfQuestions, byte answerCreatorId, byte[] data){
        return new WifiBTQuestionarePdu(TYPE.QUIZ_ANSWER, quizId, questionFormat, numberOfQuestions, answerCreatorId, data);
    }

    public static WifiBTQuestionarePdu getPollQuestionPdu(byte pollId, byte questionFormat, byte numberOfQuestions, byte[] data){
        return new WifiBTQuestionarePdu(TYPE.POLL_QUESTION, pollId, questionFormat, numberOfQuestions, (byte) 0, data);

    }

    public static WifiBTQuestionarePdu getPollAnswerPdu(byte pollId, byte questionFormat, byte numberOfQuestions, byte answerCreatorId, byte[] data){
        return new WifiBTQuestionarePdu(TYPE.POLL_ANSWER, pollId, questionFormat, numberOfQuestions, answerCreatorId, data);
    }

    public static ApplicationLayerPdu from(LlMessage llmessage) {
        return decode(llmessage.getData());

    }


    public static ApplicationLayerPdu from(BtMessage btMessage) {
        return decode(btMessage.getBody());

    }

    public static ApplicationLayerPdu from(WifiMessage wifiMessage) {

        WifiBTQuestionarePdu pdu = decode(wifiMessage.getBody());
        pdu.setToAddr(wifiMessage.getFromAddress());
        pdu.setLinkId(wifiMessage.getMsgId());
        return pdu;

    }


}
