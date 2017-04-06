package com.sparq.application.userinterface;

/**
 * Created by sarahcs on 4/6/2017.
 */

public interface NotifyPollHandler {

    // TODO: I have defined this in poll fragment but it gets called only on moving from ABOUT tab to POLL tab. If the user is in the POLL tab it does not get invoked. Invoke it from POLL TAB also
    public void handlePollQuestions();
    public void handlePollAnswers();
}
