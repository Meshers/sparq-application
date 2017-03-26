package com.sparq.application.layer.pdu;

import android.util.Log;

import java.nio.charset.Charset;

import test.com.blootoothtester.network.linklayer.LlMessage;

/**
 * Created by sarahcs on 3/20/2017.
 */

public class ThreadPdu extends ApplicationLayerPdu{

    private final static int THREAD_ID_BYTES = 1;
    private final static int THREAD_CREATOR_ID_BYTES = 1;
    private final static int SUB_THREAD_CREATOR_ID_BYTES = 1;
    private final static int SUB_THREAD_ID_BYTES = 1;

    private final static int PDU_QUESTION_HEADER_BYTES = TYPE_BYTES + THREAD_CREATOR_ID_BYTES + THREAD_ID_BYTES;
    private final static int PAYLOAD_QUESTION_MAX_BYTES = TOT_SIZE - PDU_QUESTION_HEADER_BYTES;

    private final static int PDU_ANSWER_HEADER_BYTES = TYPE_BYTES + THREAD_CREATOR_ID_BYTES + THREAD_ID_BYTES + SUB_THREAD_CREATOR_ID_BYTES+ SUB_THREAD_ID_BYTES;
    private final static int PAYLOAD_ANSWER_MAX_BYTES = TOT_SIZE - PDU_ANSWER_HEADER_BYTES;

    private final static int PDU_QUESTION_VOTE_HEADER_BYTES = TYPE_BYTES + THREAD_CREATOR_ID_BYTES + THREAD_ID_BYTES;
    private final static int PAYLOAD_QUESTION_VOTE_MAX_BYTES = TOT_SIZE - PDU_QUESTION_VOTE_HEADER_BYTES;

    private final static int PDU_ANSWER_VOTE_HEADER_BYTES = TYPE_BYTES + THREAD_CREATOR_ID_BYTES + THREAD_ID_BYTES + SUB_THREAD_CREATOR_ID_BYTES + SUB_THREAD_ID_BYTES;
    private final static int PAYLOAD_ANSWER_VOTE_MAX_BYTES = TOT_SIZE - PDU_ANSWER_VOTE_HEADER_BYTES;

    private final static int PAYLOAD_MAX_BYTES = Math.max(
            PAYLOAD_QUESTION_MAX_BYTES,
            Math.max(PAYLOAD_ANSWER_MAX_BYTES,
                    Math.max(PAYLOAD_QUESTION_VOTE_MAX_BYTES,
                            PAYLOAD_ANSWER_VOTE_MAX_BYTES))
    );

    public final static int HEADER_MAX_BYTES = Math.max(
            PDU_QUESTION_HEADER_BYTES,
            Math.max(PDU_ANSWER_HEADER_BYTES,
                    Math.max(PDU_QUESTION_VOTE_HEADER_BYTES,
                            PDU_ANSWER_VOTE_HEADER_BYTES))
    );

    private TYPE mType;
    private byte mCreatorId;
    private byte mThreadId;
    private byte mSubThreadCreatorId;
    private byte mSubThreadId;
    private byte[] mData;

    private ThreadPdu(TYPE type, byte creatorId, byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data) {
        super(type);

        this.mType = type;
        this.mCreatorId = creatorId;
        this.mThreadId = threadId;
        this.mSubThreadCreatorId = subThreadCreatorId;
        this.mSubThreadId = subThreadId;
        this.mData = data;

        if (mData.length > PAYLOAD_MAX_BYTES) {
            throw new IllegalArgumentException("Payload size greater than max (received "
                    + data.length + " max " + PAYLOAD_MAX_BYTES + " bytes)");
        }

        this.mData = data;
    }

    public static ThreadPdu getQuestionPdu(byte threadCreatorId, byte threadId, byte[] data){
        return new ThreadPdu(TYPE.QUESTION, threadCreatorId, threadId, (byte) 0, (byte) 0, data);
    }

    public static ThreadPdu getAnswerPdu(byte threadCreatorId, byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data){
        return new ThreadPdu(TYPE.ANSWER, threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);
    }

    public static ThreadPdu getQuestionVotePdu(byte threadCreatorId, byte threadId, byte[] data){

        return new ThreadPdu(TYPE.QUESTION_VOTE, threadCreatorId, threadId, (byte) 0, (byte) 0, data);
    }

    public static ThreadPdu getAnswerVotePdu(byte creatorId, byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data){
        return new ThreadPdu(TYPE.ANSWER_VOTE, creatorId, threadId, subThreadCreatorId, subThreadId, data);
    }

    public static boolean isValidPdu(String encoded) {
        return encoded != null && isValidPdu(encoded.getBytes(CHARSET));
    }

    public static boolean isValidPdu(byte[] encoded) {

        if (encoded.length < TYPE_BYTES + THREAD_ID_BYTES) {
            return false;
        }

        return true;
    }

    public byte getThreadCreatorId(){
        return this.mCreatorId;
    }

    public byte getThreadId(){
        return this.mThreadId;
    }

    public byte getSubThreadId(){
        return this.mSubThreadId;
    }

    public byte getSubThreadCreatorId(){
        return this.mSubThreadCreatorId;
    }

    public byte[] getData(){
        return this.mData;
    }

    public String getContent(){
        return new String (mData, CHARSET);
    }

    public String getAsString() {
        return new String(encode(), CHARSET);
    }

    @Override
    public byte[] encode() {
        int headerSize = 0;
        switch (getType()) {
            case QUESTION:
                headerSize = PDU_QUESTION_HEADER_BYTES;
                break;
            case ANSWER:
                headerSize = PDU_ANSWER_HEADER_BYTES;
                break;
            case QUESTION_VOTE:
                headerSize = PDU_QUESTION_VOTE_HEADER_BYTES;
                break;
            case ANSWER_VOTE:
                headerSize = PDU_ANSWER_VOTE_HEADER_BYTES;
                break;
        }
        byte[] encoded = new byte[headerSize + mData.length];

        int nextFieldIndex = 0;

        // add Type
        encoded[nextFieldIndex] = getTypeEncoded(mType);
        nextFieldIndex += TYPE_BYTES;

        //add thread creator Id
        encoded[nextFieldIndex] = getThreadCreatorId();
        nextFieldIndex += THREAD_CREATOR_ID_BYTES;

        //add thread ID
        encoded[nextFieldIndex] = getThreadId();
        nextFieldIndex += THREAD_ID_BYTES;

        switch(getType()){
            case QUESTION:
                break;
            case ANSWER:
            case ANSWER_VOTE:
                encoded[nextFieldIndex] = getSubThreadCreatorId();
                nextFieldIndex += SUB_THREAD_CREATOR_ID_BYTES;

                encoded[nextFieldIndex] = getSubThreadId();
                nextFieldIndex += SUB_THREAD_ID_BYTES;

        }

        // add the actual data to send
        System.arraycopy(mData, 0, encoded, nextFieldIndex, mData.length);
        return encoded;
    }


    public static ThreadPdu decode(byte[] encoded) {

        int nextFieldIndex = 0;
        byte creatorID = (byte) 1;
        byte subThreadId = (byte) 1;
        byte subThreadCreatorId = (byte) 0;

        // get type
        TYPE type = getTypeDecoded(encoded[nextFieldIndex]);


        if(type != null){
            nextFieldIndex += TYPE_BYTES;

            //get thread creator id
            creatorID = encoded[nextFieldIndex];
            nextFieldIndex += THREAD_CREATOR_ID_BYTES;

            // get threadID
            byte threadID = encoded[nextFieldIndex];
            nextFieldIndex += THREAD_ID_BYTES;

            switch(type){
                case ANSWER:
                case ANSWER_VOTE:

                    subThreadCreatorId = encoded[nextFieldIndex];
                    nextFieldIndex += SUB_THREAD_CREATOR_ID_BYTES;

                    subThreadId = encoded[nextFieldIndex];
                    nextFieldIndex += SUB_THREAD_ID_BYTES;
                    break;
            }

            // get the actual data
            byte[] data = new byte[encoded.length - nextFieldIndex];
            System.arraycopy(encoded, nextFieldIndex, data, 0, data.length);

            return new ThreadPdu(type, creatorID, threadID, subThreadCreatorId, subThreadId, data);
        }

        return null;
    }


    public static ApplicationLayerPdu from(LlMessage llmessage) {
        return decode(llmessage.getData());

    }
}

