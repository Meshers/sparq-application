package com.sparq.quizpolls.application.layer.almessage;

import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by aphasingnirvana on 20/3/17.
 */

public class AlQuestion extends AlMessage{

    private byte mCreatorId;
    private byte mQuestionId;
    private byte[] mQuestionData;
    private boolean isDummy;
    private ArrayList<AlVote> mVotes;

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public AlQuestion(byte creatorId, byte questionId, byte[] data, boolean dummy){
        super(ApplicationLayerPdu.TYPE.QUESTION);
        mCreatorId = creatorId;
        mQuestionId = questionId;
        mQuestionData = data;
        this.isDummy = dummy;

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

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public void setVotes(ArrayList<AlVote> votes){
        this.mVotes = votes;
    }

    public AlVote getVoteAtIndex(int index){
        return this.mVotes.get(index);
    }

    public void addVote(AlVote vote){
        this.mVotes.add(vote);
    }

    public void copyData(AlQuestion question){

        this.mQuestionData = question.getData();
        this.isDummy = false;
    }

    /**
     * override the equals and hashCode functions
     */

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            AlQuestion alQuestion = (AlQuestion) object;
            if (this.mCreatorId == alQuestion.getCreatorId()
                    && this.mQuestionId == alQuestion.getQuestionId()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.mCreatorId;
        hash = 7 * hash + this.mQuestionId;
        return hash;
    }
}