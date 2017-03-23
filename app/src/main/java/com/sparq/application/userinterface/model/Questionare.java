package com.sparq.application.userinterface.model;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by sarahcs on 2/12/2017.
 */

public abstract class Questionare {

    int questionareId;
    int eventId;
    /*
     * 0: Quiz
     * 1: Poll
     * 2: Conversation Thread
     */
    int type;
    String name;
    String description;
    Date date;
    UserItem creator;
    HashMap<Integer,QuestionItem> questions;

    public Questionare(){
        questions = new HashMap<Integer, QuestionItem>();
    }

    public Questionare(int questionareId, int eventId, int type, String name, String description, Date date, UserItem creator, int numberOfQuestions){
        this.questionareId = questionareId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.eventId = eventId;
        this.date = date;
        this.creator = creator;

        questions = new HashMap<Integer, QuestionItem>(numberOfQuestions);

        for(int i = 0; i < numberOfQuestions; i++){
            questions.put(i, null);
        }
    }

    public int getQuestionareId() {
        return questionareId;
    }

    public void setQuestionareId(int questionareId) {
        this.questionareId = questionareId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserItem getCreator() {
        return creator;
    }

    public void setCreator(UserItem creator) {
        this.creator = creator;
    }

    public HashMap<Integer, QuestionItem> getQuestions() {
        return questions;
    }

    public void setQuestions(HashMap<Integer, QuestionItem> questions) {
        this.questions = questions;
    }

    public boolean addQuestionToList(int questionId, QuestionItem questionItem){

        if(questions.containsKey(questionId)){
            return false;
        }
        else{
            questions.put(questionId, questionItem);
            return true;
        }
    }

    public void addQuestionToList(QuestionItem questionItem){
        questions.put(questionItem.getQuestionId(), questionItem);
    }

    public QuestionItem getQuestionWithKey(int questionId){
        return questions.get(questionId);
    }

    public boolean editQuestion(int questionId, int questionareId, String question, QuestionItem.FORMAT format, double totalMarks) {

        if(!questions.containsKey(questionId)){
            return false;
        }
        else{
            QuestionItem questionItem = new QuestionItem(questionId, questionareId, question, format, totalMarks);
            questions.put(questionId, questionItem);
            return true;
        }
    }

    public boolean removeQuestion(int questionId){

        if(questions.remove(questionId) != null){
            return true;
        }
        else{
            return false;
        }
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }


}
