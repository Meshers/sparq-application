package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlQuestion extends AlMessage{

    private byte mCreatorId;
    private byte mQuestionId;
    private byte[] mQuestionData;
    private ArrayList<AlVote> mVotes;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlQuestion(byte creatorId, byte questionId, byte[] data){
        super(ApplicationLayerPdu.TYPE.QUESTION);
        mCreatorId = creatorId;
        mQuestionId = questionId;
        mQuestionData = data;

        mVotes = new ArrayList<>();
    }

    public byte getCreatorId() {
        return mCreatorId;
    }

    public byte getQuestionId() {
        return mQuestionId;
    }

    public byte[] getCombinedQuestionId(){
        return new byte[]{mCreatorId, mQuestionId };
    }

    public byte[] getData() {
        return mQuestionData;
    }

    public String getDataAsString() {
        return new String(mQuestionData, CHARSET);
    }

    public ArrayList<AlVote> getVotes(){
        return this.mVotes;
    }

    public AlVote getVoteAtIndex(int index){
        return this.mVotes.get(index);
    }

    public void addVote(AlVote vote){
        this.mVotes.add(vote);
    }
}