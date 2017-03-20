package com.sparq.application.userinterface.model;

import java.util.Date;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class ConversationThread extends Questionare {

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    private String question;

    public ConversationThread(){
        super();
    }

    public ConversationThread(int threadId, int eventId, Date date, UserItem creator, String question){
        super(threadId, eventId, 2, null, null, date, creator);
        this.question = question;

    }
}
