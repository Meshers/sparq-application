package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlAnswer extends AlMessage{
    private byte mCreatorId;
    private byte mQuestionId;
    private byte mAnswerCreatorId;
    private byte mAnswerId;
    private byte[] mAnswerData;
    private ArrayList<AlVote> mVotes;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlAnswer(byte creatorId, byte questionId, byte answerCreatorId, byte answerId, byte[] data) {
        super(ApplicationLayerPdu.TYPE.ANSWER);
        mCreatorId = creatorId;
        mQuestionId = questionId;
        mAnswerCreatorId = answerCreatorId;
        mAnswerId = answerId;
        mAnswerData = data;

        mVotes = new ArrayList<>();

    }

    public byte getCreatorId(){
        return mCreatorId;
    }

    public byte getQuestionId(){
        return mQuestionId;
    }

    public byte getAnswerId(){
        return mAnswerId;
    }

    public byte getAnswerCreatorId(){
        return mAnswerCreatorId;
    }

    public byte[] getCombinedAnswerId(){
        return new byte[]{mAnswerCreatorId, mAnswerId};
    }

    public byte[] getAnswerData() {
        return mAnswerData;
    }

    public String getAnswerDataAsString() {
        return new String(mAnswerData, CHARSET);
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