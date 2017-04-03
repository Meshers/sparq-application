package com.sparq.application.userinterface.model;

import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sarahcs on 2/13/2017.
 */

public class AnswerItem implements Serializable{

    private int answerId;
    private int questionItemId;
    private int length;
    private int votes;
    private boolean hasVoted;
    private UserItem threadCreator;
    private UserItem answerCreator;
    private QuestionItem.FORMAT format;

    private String answer;
    private int answerChoice;
    private ArrayList<Integer> answerChoices;


    public AnswerItem() {
    }

    public AnswerItem(int answerId, UserItem answerCreator, int questionItemId, UserItem threadCreator,
                      QuestionItem.FORMAT format, String answer, int answerChoice, ArrayList<Integer> answerChoices,int votes) {
        this.answerId = answerId;
        this.questionItemId = questionItemId;
        this.length = answer.length();
        this.format = format;
        this.answerCreator = answerCreator;
        this.threadCreator = threadCreator;
        this.votes = votes;
        this.hasVoted = false;

        switch(format){
            case MCQ_SINGLE:
                this.answerChoice = answerChoice;
                break;
            case MCQ_MULTIPLE:
                answerChoices = answerChoices;
                break;
            case ONE_WORD:
                this.answer = answer;
                break;
            case SHORT:
                this.answer = answer;
                break;
        }
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

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public static AnswerItem getMCQSingleAnswer(int answerId, UserItem answerCreator, int questionItemId, UserItem questionCreator,
                                                int answerChoice){
        return new AnswerItem(
                answerId,
                answerCreator,
                questionItemId,
                questionCreator,
                QuestionItem.FORMAT.MCQ_SINGLE,
                null,
                answerChoice,
                null,
                Constants.INITIAL_VOTE_COUNT
        );
    }

    public static AnswerItem getMCQMultipleAnswer(int answerId, UserItem answerCreator, int questionItemId, UserItem questionCreator,
                                                ArrayList<Integer> answerChoices){
        return new AnswerItem(
                answerId,
                answerCreator,
                questionItemId,
                questionCreator,
                QuestionItem.FORMAT.MCQ_MULTIPLE,
                null,
                -1,
                answerChoices,
                Constants.INITIAL_VOTE_COUNT
        );
    }

    public static AnswerItem getMCQOneWordAnswer(int answerId, UserItem answerCreator, int questionItemId, UserItem questionCreator,
                                                String answer){
        return new AnswerItem(
                answerId,
                answerCreator,
                questionItemId,
                questionCreator,
                QuestionItem.FORMAT.ONE_WORD,
                answer,
                -1,
                null,
                Constants.INITIAL_VOTE_COUNT
        );
    }

    public static AnswerItem getShortAnswer(int answerId, UserItem answerCreator, int questionItemId, UserItem questionCreator,
                                                 String answer){
        return new AnswerItem(
                answerId,
                answerCreator,
                questionItemId,
                questionCreator,
                QuestionItem.FORMAT.SHORT,
                answer,
                -1,
                null,
                Constants.INITIAL_VOTE_COUNT
        );
    }

    public static AnswerItem getAnswerItemFromMessage(AlAnswer alAnswer){
        return getShortAnswer(
                alAnswer.getAnswerId(),
                new UserItem(alAnswer.getAnswerCreatorId()),
                alAnswer.getQuestionId(),
                new UserItem(alAnswer.getCreatorId()),
                alAnswer.getAnswerDataAsString()
        );
    }

}
