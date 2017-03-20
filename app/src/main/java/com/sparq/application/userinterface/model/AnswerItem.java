package com.sparq.application.userinterface.model;

import java.io.Serializable;

/**
 * Created by sarahcs on 2/13/2017.
 */

public class AnswerItem implements Serializable{

    private int answerId;
    private int questionItemId;
    private String answer;
    private int length;
    private UserItem user;


    public AnswerItem() {
    }

    public AnswerItem(int answerId, int questionItemId, String answer, UserItem user) {
        this.answerId = answerId;
        this.questionItemId = questionItemId;
        this.length = answer.length();
        this.answer = answer;
        this.user = user;
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


    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }
}
