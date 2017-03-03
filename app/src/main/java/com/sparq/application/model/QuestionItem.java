package com.sparq.application.model;

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
    private int format;
    private double totalMarks;
    private HashMap<Integer, String> options;

    public QuestionItem(int questionId, int questionareId, String question, int format, double totalMarks) {
        this.questionId = questionId;
        this.questionareId = questionareId;
        this.question = question;
        this.format = format;
        this.totalMarks = totalMarks;

        switch(this.format){
            case 1:
            case 2:
                options = new HashMap<Integer, String>();
                break;
        }
    }

    public QuestionItem(int questionId, int questionareId, String question, int format, double totalMarks, HashMap<Integer, String> options) {
        this(questionId, questionareId, question, format, totalMarks);

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

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
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
}
