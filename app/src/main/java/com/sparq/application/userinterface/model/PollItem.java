package com.sparq.application.userinterface.model;

import java.util.Date;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class PollItem extends Questionare{

    /*
     *0: play
     *1: pause
     *2: stop
     */
    private int state;

    public PollItem(){
        super();
    }

    public PollItem(int pollId, int eventId, String name, String description, Date date, int state, UserItem creator){
        super(pollId, eventId, 1, name, description, date, creator, 0);
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
