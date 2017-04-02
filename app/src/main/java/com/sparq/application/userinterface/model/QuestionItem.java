package com.sparq.application.userinterface.model;

import com.sparq.util.Constants;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sarahcs on 2/12/2017.
 */
public class QuestionItem {

    private int questionId;
    private int questionareId;
    private String question;
    /*
     * 0: none
     * 1: MCQ (Single Option)
     * 2: MCQ (Multiple Option)
     * 3: One Word Answers
     * 4: Short Answers
     */
    enum FORMAT{
        NONE,
        MCQ_SINGLE,
        MCQ_MULTIPLE,
        ONE_WORD,
        SHORT
    }

    private FORMAT format;
    private double totalMarks;
    private int votes;
    private boolean hasVoted;
    private HashMap<Integer, String> options;

    public QuestionItem(int questionId, int questionareId, String question, FORMAT format, double totalMarks, int votes) {
        this.questionId = questionId;
        this.questionareId = questionareId;
        this.question = question;
        this.format = format;
        this.totalMarks = totalMarks;
        this.votes = votes;
        this.hasVoted = false;

        switch(getFormat()){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                options = new HashMap<Integer, String>();
                break;
        }
    }

    public QuestionItem(int questionId, int questionareId, String question, FORMAT format, double totalMarks,
                        HashMap<Integer, String> options, int votes) {
        this(questionId, questionareId, question, format, totalMarks, votes);

        this.options = options;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionareId() {
        return questionareId;
    }

    public void setQuestionareId(int questionareId) {
        this.questionareId = questionareId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public FORMAT getFormat() {
        return format;
    }

    public void setFormat(FORMAT format) {
        this.format = format;
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

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public HashMap<Integer, String> getOptions() {
        return options;
    }

    public void setOptions(HashMap<Integer, String> options) {
        this.options = options;
    }

    public void addOption(int optionId, String option){
        options.put(optionId, option);
    }

    public static QuestionItem getQuizQuestion(int questionId, int questionareId, String question,
                                               FORMAT format, double totalMarks,
                                               HashMap<Integer, String> options){
        return new QuestionItem(questionId, questionareId, question, format, totalMarks, options, Constants.INITIAL_VOTE_COUNT);
    }

    public static QuestionItem getPollQuestion(int questionId, int questionareId, String question,
                                               FORMAT format,
                                               HashMap<Integer, String> options){
        return new QuestionItem(questionId, questionareId, question, format, Constants.MIN_QUESTION_MARKS, options, Constants.INITIAL_VOTE_COUNT);
    }

    public static QuestionItem getThreadQuestion(int questionId, int questionareId, String question){
        return new QuestionItem(questionId, questionareId, question, FORMAT.SHORT, Constants.MIN_QUESTION_MARKS, null, Constants.INITIAL_VOTE_COUNT);
    }
}
