package com.sparq.application.userinterface.model;

import java.util.Date;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class QuizItem extends Questionare{

    private long duration;
    private double totalMarks;
    /*
         * 0: inactive
         * 1: active
         */
    private int state;

    public QuizItem(){
        super();
    }

    public QuizItem(int quizId, int eventId, String name, String description, Date date, long duration, int state, double totalMarks, UserItem creator){
        super(quizId, eventId, 0, name, description, date, creator);

        this.state = state;
        this.duration = duration;
        this.totalMarks = totalMarks;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
