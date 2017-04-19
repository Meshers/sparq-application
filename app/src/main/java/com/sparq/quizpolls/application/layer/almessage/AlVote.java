package com.sparq.quizpolls.application.layer.almessage;

/**
 * Created by sarahcs on 3/20/2017.
 */

import android.util.Log;

import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlVote extends AlMessage{

    private static final String TAG = "AlVote";

    private static final int VOTE_DATA_SIZE = 1;

    public enum VOTE_TYPE {
        UPVOTE (1),
        DOWNVOTE (2);

        private int voteType;

        VOTE_TYPE(int numVal) {
            this.voteType = numVal;
        }

        public int getType() {
            return voteType;
        }
    }

    enum VOTE_PARENT{
        QUESTION_VOTE,
        ANSWER_VOTE;
    }
    private byte mCreatorId;
    private byte mQuestionId;
    private byte mAnswerCreatorId;
    private byte mAnswerId;
    private VOTE_PARENT mVoteParent;
    private byte[] mData;

    public AlVote(byte creatorId, byte questionId, byte answerCreatorId, byte answerId, ApplicationLayerPdu.TYPE type, VOTE_PARENT voteParent, byte[] data) {
        super(type);
        mVoteParent = voteParent;
        mCreatorId = creatorId;
        mQuestionId = questionId;
        mAnswerCreatorId = answerCreatorId;
        mAnswerId = answerId;
        mData = data;
    }

    public static AlVote getQuestionVote(byte creatorId, byte questionId, byte[] data){
        return new AlVote(creatorId, questionId, (byte)0, (byte) 0, ApplicationLayerPdu.TYPE.QUESTION_VOTE, VOTE_PARENT.QUESTION_VOTE, data);
    }

    public static AlVote getAnswerVote(byte creatorId, byte questionId, byte answerCreatorId, byte answerId, byte[] data){
        return new AlVote(creatorId, questionId, answerCreatorId, answerId, ApplicationLayerPdu.TYPE.ANSWER_VOTE, VOTE_PARENT.ANSWER_VOTE, data);
    }

    public int getVoteParent(){
        return mVoteParent.ordinal();
    }

    public byte getCreatorId(){
        return mCreatorId;
    }

    public byte getQuestionId(){
        return mQuestionId;
    }

    public byte getAnswerCreatorId(){
        return mAnswerCreatorId;
    }

    public byte getAnswerId(){
        return mAnswerId;
    }

    public VOTE_TYPE getVoteValue(){
        try{

            if(mData.length != VOTE_DATA_SIZE){
                throw new IllegalArgumentException("Vote data size beyond vote packet data limit (received "
                        + mData.length + " allowed " + VOTE_DATA_SIZE + " bytes)");
            }

            return VOTE_TYPE.values()[mData[0] - 1];
        }
        catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static byte getVoteEncoded(VOTE_TYPE type){
        return (byte) (type.ordinal() + 1) ;
    }

    public static VOTE_TYPE getVoteDecoded(byte type){
        return VOTE_TYPE.values()[type - 1] ;
    }

}
