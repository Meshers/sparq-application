package com.sparq.application.userinterface.model;

import com.sparq.util.Constants;

import java.util.ArrayList;
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
    public enum FORMAT{
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
    private ArrayList<String> options;

    public QuestionItem(int questionId, int questionareId, String question, FORMAT format, double totalMarks, int votes) {
        this.questionId = questionId;
        this.questionareId = questionareId;
        this.question = question;
        this.format = format;
        this.totalMarks = totalMarks;
        this.votes = votes;
        this.hasVoted = false;

    }

    public QuestionItem(int questionId, int questionareId, String question, FORMAT format, double totalMarks,
                        ArrayList<String> options, int votes) {
        this(questionId, questionareId, question, format, totalMarks, votes);

        switch(getFormat()){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                this.options = options;
                break;
        }
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

    public static FORMAT getFormatFromByte(byte format){
        return FORMAT.values()[(int) format];
    }

    public static byte getFormatAsByte(FORMAT format){
        return (byte) format.ordinal();
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

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public void addOption(String option){
        options.add(option);
    }

    public static QuestionItem getMCQSingleQuestion(int questionId, int questionareId, String question, ArrayList<String> options, float totalMarks){

        return new QuestionItem(questionId, questionareId, question, FORMAT.MCQ_SINGLE, totalMarks, options, Constants.INITIAL_VOTE_COUNT);
    }

    public static QuestionItem getMCQMultipleQuestion(int questionId, int questionareId, String question, ArrayList<String> options, float totalMarks){

        return new QuestionItem(questionId, questionareId, question, FORMAT.MCQ_MULTIPLE, totalMarks, options, Constants.INITIAL_VOTE_COUNT);
    }

    public static QuestionItem getOneWordQuestion(int questionId, int questionareId, String question, float totalMarks){

        return new QuestionItem(questionId, questionareId, question, FORMAT.ONE_WORD, totalMarks, Constants.INITIAL_VOTE_COUNT);
    }


    public static QuestionItem getShortQuestion(int questionId, int questionareId, String question, float totalMarks){
        return new QuestionItem(questionId, questionareId, question, FORMAT.SHORT, totalMarks, null, Constants.INITIAL_VOTE_COUNT);
    }
}
