package com.sparq.application.layer.pdu;

import com.sparq.application.userinterface.model.QuestionItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Build;

import java.util.BitSet;

import test.com.blootoothtester.network.linklayer.LlMessage;
import test.com.blootoothtester.util.Constants;

import static com.sparq.util.Constants.CONSTANT_DELIMITER;

/**
 * Created by sarahcs on 3/30/2017.
 */

public class PollPdu extends ApplicationLayerPdu {

    private final static int POLL_ID_BYTES = 1;
    private final static int QUESTION_ID_BYTES = 1;
    private final static int QUESTION_FORMAT_BYTES = 1;
    private final static int QUESTION_CREATOR_ID_BYTES = 1;
    public final static int ANSWER_CREATOR_ID_BYTES = 1;
    public final static int HEADER_SIZE_BYTES = 1;
    public final static int FLAG_BYTES = 1;

    public final static int MIN_OPTION_BYTES = 0;
    public final static int MAX_OPTION_BYTES = 50;


    private final static int PDU_POLL_QUESTION_HEADER_MAX_BYTES = TYPE_BYTES + POLL_ID_BYTES + QUESTION_CREATOR_ID_BYTES + QUESTION_ID_BYTES + QUESTION_FORMAT_BYTES + HEADER_SIZE_BYTES +FLAG_BYTES + MAX_OPTION_BYTES;
    public final static int PDU_POLL_QUESTION_HEADER_MIN_BYTES = TYPE_BYTES + POLL_ID_BYTES + QUESTION_CREATOR_ID_BYTES + QUESTION_ID_BYTES + QUESTION_FORMAT_BYTES + HEADER_SIZE_BYTES + FLAG_BYTES + MIN_OPTION_BYTES;

    private final static int PAYLOAD_POLL_QUESTION_MAX_BYTES = TOT_SIZE - PDU_POLL_QUESTION_HEADER_MIN_BYTES;
    private final static int PAYLOAD_POLL_QUESTION_MIN_BYTES = TOT_SIZE - PDU_POLL_QUESTION_HEADER_MAX_BYTES;


    public final static int PDU_POLL_ANSWER_HEADER_BYTES = TYPE_BYTES + POLL_ID_BYTES + QUESTION_CREATOR_ID_BYTES + QUESTION_ID_BYTES + QUESTION_FORMAT_BYTES + ANSWER_CREATOR_ID_BYTES ;
    private final static int PAYLOAD_POLL_ANSWER_BYTES = TOT_SIZE - PDU_POLL_ANSWER_HEADER_BYTES;


    private final static int PAYLOAD_POLL_MAX_BYTES = Math.max(
            PAYLOAD_POLL_QUESTION_MAX_BYTES, PAYLOAD_POLL_ANSWER_BYTES
    );

    public final static int HEADER_POLL_MAX_BYTES = Math.max(
            PDU_POLL_QUESTION_HEADER_MAX_BYTES, PDU_POLL_ANSWER_HEADER_BYTES
    );

    public final static int FLAGS_POLL_MAX_BITS = 3;

    private TYPE mType;
    private byte mPollId;
    private byte mCreatorId;
    private byte mQuestionId;
    private byte mQuestionFormat;
    private byte mAnswerCreatorId;
    private byte mHeaderSize;
    private BitSet mFlags;
    private byte[] mData;
    private byte[][] mOptions;

    private PollPdu(TYPE type, byte pollId, byte creatorId, byte questionId, byte questionFormat, byte answerCreatorId,
                    boolean hasMore, boolean endOfPoll, boolean isMainQuestion, byte[] data, byte[]... options) {
        super(type);

        this.mType = type;
        this.mPollId = pollId;
        this.mCreatorId = creatorId;
        this.mQuestionId = questionId;
        this.mQuestionFormat = questionFormat;
        this.mAnswerCreatorId = answerCreatorId;

        // initialize the bitset
        mFlags = new BitSet(2);

        mFlags.set(0, hasMore);
        mFlags.set(1, endOfPoll);
        mFlags.set(2, isMainQuestion);

        int headerSize = 0;

        switch(mType){
            case POLL_QUESTION:

                headerSize = PDU_POLL_QUESTION_HEADER_MIN_BYTES;
                if(options != null){

                    mOptions = options;

                    for(byte[] option: mOptions){
                        headerSize += option.length + 1;
                    }
                }

                break;
            case POLL_ANSWER:
                headerSize = PDU_POLL_ANSWER_HEADER_BYTES;
                break;
        }

        // initialize header size bytes
        mHeaderSize = (byte) headerSize;

        this.mData = data;

        if (mData.length > PAYLOAD_POLL_MAX_BYTES) {
            throw new IllegalArgumentException("Payload size greater than max (received "
                    + data.length + " max " + PAYLOAD_POLL_MAX_BYTES + " bytes)");
        }


        if (headerSize > HEADER_POLL_MAX_BYTES) {
            throw new IllegalArgumentException("Header size greater than max (received "
                    + headerSize + " max " + HEADER_POLL_MAX_BYTES + " bytes)");
        }
    }

    public static PollPdu getQuestionPdu(byte pollId, byte questionCreatorId, byte questionId, byte questionFormat,
                                         boolean hasMore, boolean endOfPoll, boolean isMainQuestion, byte[] data, byte[]... options){
        return new PollPdu(TYPE.POLL_QUESTION, pollId, questionCreatorId, questionId, questionFormat, (byte) 0, hasMore, endOfPoll, isMainQuestion,data, options);

    }

    public static PollPdu getAnswerPdu(byte pollId, byte questionCreatorId, byte questionId, byte format,byte answerCreatorId, byte[] data){
        return new PollPdu(TYPE.POLL_ANSWER, pollId, questionCreatorId, questionId, format, answerCreatorId, false, false, false, data);
    }

    public static boolean isValidPdu(String encoded) {
        return encoded != null && isValidPdu(encoded.getBytes(CHARSET));
    }

    public static boolean isValidPdu(byte[] encoded) {

        if (encoded.length < TYPE_BYTES + POLL_ID_BYTES) {
            return false;
        }

        return true;
    }

    public byte getPollId(){
        return this.mPollId;
    }

    public byte getQuestionCreatorId(){
        return this.mCreatorId;
    }

    public byte getQuestionId(){
        return this.mQuestionId;
    }

    public byte getQuestionFormat(){ return this.mQuestionFormat; }

    public byte getHeaderSize(){ return this.mHeaderSize; }

    public byte getAnswerCreatorId(){
        return this.mAnswerCreatorId;
    }

    public boolean hasMore(){
        return mFlags.get(0);
    }

    public boolean endOfPoll(){
        return mFlags.get(1);
    }

    public boolean isMainQuestion(){
        return mFlags.get(2);
    }

    public byte[][] getOptions(){
        return this.mOptions;
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

    public static int getIntegerHeaderSize(byte headerSize){
        return   (int) headerSize;
    }

    @SuppressLint("NewApi")
    @Override
    public byte[] encode() {
        int headerSize = 0;

        if(mOptions != null){
            mOptions = addOptionsDelimiter(mOptions);
        }

        switch (getType()) {
            case POLL_QUESTION:
                headerSize = getIntegerHeaderSize(mHeaderSize);
                break;
            case POLL_ANSWER:
                headerSize = PDU_POLL_ANSWER_HEADER_BYTES;
                break;
        }
        byte[] encoded = new byte[headerSize + mData.length];

        int nextFieldIndex = 0;

        // add Type
        encoded[nextFieldIndex] = getTypeEncoded(mType);
        nextFieldIndex += TYPE_BYTES;

        //add poll id
        encoded[nextFieldIndex] = getPollId();
        nextFieldIndex += POLL_ID_BYTES;

        //add question creator Id
        encoded[nextFieldIndex] = getQuestionCreatorId();
        nextFieldIndex += QUESTION_CREATOR_ID_BYTES;

        //add question ID
        encoded[nextFieldIndex] = getQuestionId();
        nextFieldIndex += QUESTION_ID_BYTES;

        //add the question format
        encoded[nextFieldIndex] = getQuestionFormat();
        nextFieldIndex += QUESTION_FORMAT_BYTES;

        switch(getType()){
            case POLL_QUESTION:

                //add the headerSize
                encoded[nextFieldIndex] = getHeaderSize();
                nextFieldIndex += HEADER_SIZE_BYTES;

                //convert the bitset into a byte
                encoded[nextFieldIndex] = mFlags.toByteArray()[0];
                nextFieldIndex += FLAG_BYTES;

                // add the options
                if(mOptions != null){
                    for(int i = 0; i < mOptions.length; i++){
                        System.arraycopy(mOptions[i], 0, encoded, nextFieldIndex, mOptions[i].length);
                        nextFieldIndex += mOptions[i].length;
                    }
                }
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

    public static PollPdu decode(byte[] encoded) {

        int nextFieldIndex = 0;
        byte pollId = (byte) 1;
        byte questionCreatorId = (byte) 1;
        byte questionId = (byte) 1;
        byte questionFormat = (byte) 0;
        byte headerSize = (byte) 0;
        boolean hasMore = false;
        boolean endOfPoll = false;
        boolean isMainQuestion = false;
        byte answerCreatorId = (byte) 0;

        byte[][] options = null;

        // get type
        TYPE type = getTypeDecoded(encoded[nextFieldIndex]);


        if(type != null){
            nextFieldIndex += TYPE_BYTES;

            //get poll id
            pollId = encoded[nextFieldIndex];
            nextFieldIndex += POLL_ID_BYTES;

            //get question creator id
            questionCreatorId = encoded[nextFieldIndex];
            nextFieldIndex += QUESTION_CREATOR_ID_BYTES;

            // get questionID
            questionId = encoded[nextFieldIndex];
            nextFieldIndex += QUESTION_ID_BYTES;

            // get format
            questionFormat = encoded[nextFieldIndex];
            nextFieldIndex += QUESTION_FORMAT_BYTES;

            switch(type){
                case POLL_QUESTION:

                    //extract the header size
                    headerSize = encoded[nextFieldIndex];
                    nextFieldIndex += HEADER_SIZE_BYTES;

                    hasMore = ((encoded[nextFieldIndex] & 1) != 0);
                    endOfPoll = ((encoded[nextFieldIndex] & 2) != 0);
                    isMainQuestion = ((encoded[nextFieldIndex] & 4) != 0);
                    nextFieldIndex += FLAG_BYTES;

                    //get the options if any
                    if(getIntegerHeaderSize(headerSize) > PDU_POLL_QUESTION_HEADER_MIN_BYTES){

                        byte[] optionsArray = new byte[getIntegerHeaderSize(headerSize) - PDU_POLL_QUESTION_HEADER_MIN_BYTES];
                        System.arraycopy(encoded, nextFieldIndex, optionsArray, 0, optionsArray.length);

                        options = getOptions(optionsArray);
                        nextFieldIndex += (int) (getIntegerHeaderSize(headerSize) - PDU_POLL_QUESTION_HEADER_MIN_BYTES);
                    }
                    break;

                case POLL_ANSWER:
                    answerCreatorId = encoded[nextFieldIndex];
                    nextFieldIndex += ANSWER_CREATOR_ID_BYTES;
                    break;
            }

            // get the actual data
            byte[] data = new byte[encoded.length - nextFieldIndex];
            System.arraycopy(encoded, nextFieldIndex, data, 0, data.length);

            return new PollPdu(type, pollId, questionCreatorId, questionId, questionFormat,answerCreatorId, hasMore, endOfPoll, isMainQuestion,data, options);
        }

        return null;
    }

    public static byte[][] addOptionsDelimiter(byte[][] options){

        for(int i = 0; i < options.length; i++){

            byte[] newOption = new byte[options[i].length + 1];
            for(int j = 0; j < options[i].length; j++){
                newOption[j] = options[i][j];
            }
            newOption[options[i].length] = CONSTANT_DELIMITER;
            options[i] = newOption;
        }

        return options;
    }

    public static byte[][] getOptions(byte[] optionsArray){

        ArrayList<byte[]> options = new ArrayList<>(0);

        int i = 0;
        int j = 0;
        while(i < optionsArray.length){
            j = i;
            while(optionsArray[j] != CONSTANT_DELIMITER){
                j++;
            }

            byte[] option = new byte[j - i];
            System.arraycopy(optionsArray, i, option, 0, option.length);
            options.add(option);
            i = j+1;
        }

        byte[][] ret = new byte[options.size()][];
        for (int k = 0; k < ret.length; k++)
        {
            ret[k] = options.get(k);
        }
        return ret;
    }

    public static ApplicationLayerPdu from(LlMessage llmessage) {
        return decode(llmessage.getData());

    }
}

