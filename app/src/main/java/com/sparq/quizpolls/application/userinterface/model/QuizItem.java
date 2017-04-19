package com.sparq.quizpolls.application.userinterface.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class QuizItem extends Questionare{

    private long duration;
    private double totalMarks;
    private boolean hasAnswered;

    public enum QUIZ_STATE{
        ACTIVE,
        INACTIVE
    }

    private QUIZ_STATE state;
    private HashMap<Integer, ArrayList<AnswerItem>> answers;
    private HashMap<Integer, Double> userScores;

    public QuizItem(){
        super();
    }

    public QuizItem(int quizId, int eventId, String name, String description, Date date, long duration, QUIZ_STATE state, double totalMarks, UserItem creator){
        super(quizId, eventId, QUESTIONARE_TYPE.QUIZ, name, description, date, creator, 0);

        this.state = state;
        this.duration = duration;
        this.totalMarks = totalMarks;

        this.answers = new HashMap<>(0);

        this.userScores = new HashMap<>(0);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public QUIZ_STATE getState() {
        return state;
    }

    public void setState(QUIZ_STATE state) {
        this.state = state;
    }

    public static QUIZ_STATE getStateFromInteger(int state){
        return QUIZ_STATE.values()[state];
    }

    public boolean hasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    public HashMap<Integer, ArrayList<AnswerItem>> getAllAnswers(){
        return answers;
    }

    public ArrayList<AnswerItem> getAnswersForQuestion(int questionId){
        return answers.get(questionId);
    }

    public AnswerItem getAnswerForQuestionForCreator(int questionId, int answerCreatorId){

        for(AnswerItem answer : answers.get(questionId)){

            if(answer.getCreator().getUserId() == answerCreatorId){
                return answer;
            }
        }
        return null;
    }

    public void setAnswers(HashMap<Integer, ArrayList<AnswerItem>> answers){
        this.answers = answers;
    }

    public boolean setAnswersForQuestion(int questionId, ArrayList<AnswerItem> questionAnswers){
        // must check if option to send only answers is enabled
        if (questions.containsKey(questionId)) {
            answers.put(questionId, questionAnswers);
            return true;
        }
        return false;
    }

    public boolean addAnswerToQuestion(int questionId, AnswerItem answer){

        if(questions.containsKey(questionId)){

            if(answers.containsKey(questionId)){
                answers.get(questionId).add(answer);
            }
            else{
                ArrayList<AnswerItem> answerList = new ArrayList<>();
                answerList.add(answer);
                answers.put(questionId, answerList);
            }

            return true;
        }
        return false;
    }

    public boolean addAnswerArrayToQuestion(int questionId, ArrayList<AnswerItem> answerList){
        if(questions.containsKey(questionId)){

            answers.put(questionId, answerList);
            return true;
        }
        return false;
    }

    public HashMap<Integer, Double> getUserScores(){
        return  this.userScores;
    }

    public void setUserScores(int userId, int questionNumber, AnswerItem answerItem){

        QuestionItem questionItem = getQuestionWithKey(questionNumber);

        if(userScores.containsKey(userId)){
            userScores.put(userId, userScores.get(userId) + QuestionItem.evaluateQuestion(questionItem, answerItem));
        }else{
            userScores.put(userId, QuestionItem.evaluateQuestion(questionItem, answerItem));
        }

    }

}
