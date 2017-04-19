package com.sparq.quizpolls.application.userinterface.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class EventItem {

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    private int eventCode;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    private String eventName;

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    private String agenda;

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    private String venue;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private Date date;

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    private Time startTime;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long duration;

    public int getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(int presenterId) {
        this.presenterId = presenterId;
    }

    private int presenterId;

    public EventItem(){

    }

    public EventItem(int eventCode, String eventName, String agenda, String venue, Date date,Time startTime, long duration, int presenterId){
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.agenda = agenda;
        this.venue = venue;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.presenterId = presenterId;
    }

}
