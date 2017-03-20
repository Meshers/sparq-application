package com.sparq.application.layer.almessage;

/**
 * Created by sarahcs on 3/20/2017.
 */

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlQuestionVote extends AlQuestion {
    private byte mVoteType;
    private byte mQuestionCreatorId;

    public AlQuestionVote(byte voteType, byte questionCreatorId, byte questionId, byte[] data) {
        super(questionId, data);
        mVoteType = voteType;
        mQuestionCreatorId = questionCreatorId;
    }

    public byte getVoteType(){
        return mVoteType;
    }

    public byte getQuestionCreatorId(){
        return mQuestionCreatorId;
    }

    //Identifying the Question completely with it's ID and the creator's ID
    //Sarah FIXME: 20/3/17
    public byte[] getQuestionIdentifier(){
        return (new byte[]{this.getQuestionId(), mQuestionCreatorId});
    }
}
