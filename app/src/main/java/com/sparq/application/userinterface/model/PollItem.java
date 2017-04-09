package com.sparq.application.userinterface.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class PollItem extends Questionare{

    public enum POLL_STATE{
        PLAY,
        PAUSE,
        STOP
    }
    private POLL_STATE state;
    private boolean hasAnswered;
    private HashMap<Integer, ArrayList<AnswerItem>> answers;

    public PollItem(){
        super();
    }

    public PollItem(int pollId, int eventId, String name, String description, Date date, POLL_STATE state, UserItem creator){
        super(pollId, eventId, QUESTIONARE_TYPE.POLL, name, description, date, creator, 0);
        this.state = state;

        answers = new HashMap<>(0);
    }

    public POLL_STATE getState() {
        return state;
    }

    public static POLL_STATE getStateFromInteger(int state){
        return POLL_STATE.values()[state];
    }
    public void setState(POLL_STATE state) {
        this.state = state;
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

}
