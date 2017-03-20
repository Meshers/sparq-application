package com.sparq.application.layer;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlAnswerVote extends AlAnswer {
    private byte mVoteType;
    private byte mAnswerCreatorId;

    //No data here 1 byte vote type would determine the kind of vote

    public AlAnswerVote(byte voteType, byte answerCreatorId, byte answerId, byte questionId, byte questionCreatorId, byte[] answerData) {
        super(answerId, questionId, questionCreatorId, answerData);
        mVoteType = voteType;
        mAnswerCreatorId = answerCreatorId;
    }

    public byte getVoteType(){
        return mVoteType;
    }

    public byte getAnswerCreatorId() {
        return mAnswerCreatorId;
    }

    //Identifies the Answer completely with both it's ID and the answerCreatorId
    //Sarah FIXME: 20/3/17
    public byte[] getAnswerIdentifier(){
        return (new byte[]{this.getAnswerId(), mAnswerCreatorId});
    }

}

