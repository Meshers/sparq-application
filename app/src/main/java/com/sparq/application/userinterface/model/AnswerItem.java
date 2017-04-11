package com.sparq.application.userinterface.model;

import android.util.Log;

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
    private ArrayList<Integer> answerChoices;


    public AnswerItem() {
    }

    public AnswerItem(int answerId, UserItem answerCreator, int questionItemId, UserItem threadCreator,
                      QuestionItem.FORMAT format, String answer, ArrayList<Integer> answerChoices,int votes) {
        this.answerId = answerId;
        this.questionItemId = questionItemId;
        if(answer != null){
            this.length = answer.length();
        }
        this.format = format;
        this.answerCreator = answerCreator;
        this.threadCreator = threadCreator;
        this.votes = votes;
        this.hasVoted = false;

        switch(format){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                this.answerChoices = answerChoices;
                break;
            case ONE_WORD:
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


    public QuestionItem.FORMAT getFormat() {
        return format;
    }

    public void setFormat(QuestionItem.FORMAT format) {
        this.format = format;
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


    public ArrayList<Integer> getAnswerChoices() {
        return answerChoices;
    }

    public void setAnswerChoices(ArrayList<Integer> answerChoices) {
        this.answerChoices = answerChoices;
    }

    public String getAnswerChoicesAsString(){

        String answers = "";
        for(Integer answer: answerChoices){
            answers += String.valueOf(answer);
        }

        return answers;
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
                                                String answerChoice){

        ArrayList<Integer> answer = null;
        if(answerChoice != null){
            answerChoice = answerChoice.substring(0, answerChoice.length()-1);
            answer = new ArrayList<>();
            answer.add(Integer.parseInt(answerChoice));
        }

        return new AnswerItem(
                answerId,
                answerCreator,
                questionItemId,
                questionCreator,
                QuestionItem.FORMAT.MCQ_SINGLE,
                null,
                answer,
                Constants.INITIAL_VOTE_COUNT
        );
    }

    public static AnswerItem getMCQMultipleAnswer(int answerId, UserItem answerCreator, int questionItemId, UserItem questionCreator,
                                                String answerChoices){
        ArrayList<Integer> answer = null;

        if(answerChoices != null){
            answerChoices = answerChoices.substring(0, answerChoices.length()-1);
            byte[] splitChoices = answerChoices.getBytes(Constants.CHARSET);
            answer = new ArrayList<>();

            for(byte splitChoice: splitChoices){
                answer.add(Integer.valueOf(splitChoice));
            }
        }

        return new AnswerItem(
                answerId,
                answerCreator,
                questionItemId,
                questionCreator,
                QuestionItem.FORMAT.MCQ_MULTIPLE,
                null,
                answer,
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
