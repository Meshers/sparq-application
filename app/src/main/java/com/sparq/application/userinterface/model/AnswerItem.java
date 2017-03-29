package com.sparq.application.userinterface.model;

import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.util.Constants;

import java.io.Serializable;

/**
 * Created by sarahcs on 2/13/2017.
 */

public class AnswerItem implements Serializable{

    private int answerId;
    private int questionItemId;
    private String answer;
    private int length;
    private int votes;
    private UserItem threadCreator;
    private UserItem answerCreator;


    public AnswerItem() {
    }

    public AnswerItem(int answerId, UserItem answerCreator, int questionItemId, UserItem threadCreator, String answer, int votes) {
        this.answerId = answerId;
        this.questionItemId = questionItemId;
        this.length = answer.length();
        this.answer = answer;
        this.answerCreator = answerCreator;
        this.threadCreator = threadCreator;
        this.votes = votes;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getQuestionItemId() {
        return questionItemId;
    }

    public void setQuestionItemId(int questionItemId) {
        this.questionItemId = questionItemId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getVotes(){
        return this.votes;
    }

    public void addUpVote(){
        this.votes += 1;
    }

    public void addDownVote(){
        this.votes -= 1;
    }

    public UserItem getCreator() {
        return answerCreator;
    }

    public void setCreator(UserItem user) {
        this.answerCreator = user;
    }

    public static AnswerItem getAnswerItemFrommessage(AlAnswer alAnswer){
        return new AnswerItem(
                alAnswer.getAnswerId(),
                new UserItem(alAnswer.getAnswerCreatorId()),
                alAnswer.getQuestionId(),
                new UserItem(alAnswer.getCreatorId()),
                alAnswer.getAnswerDataAsString(),
                Constants.INITIAL_VOTE_COUNT
        );
    }
}
