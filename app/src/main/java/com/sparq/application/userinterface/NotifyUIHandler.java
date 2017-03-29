package com.sparq.application.userinterface;

/**
 * Created by sarahcs on 3/29/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.application.userinterface.model.AnswerItem;

import java.util.ArrayList;

public interface NotifyUIHandler {
    public void handleConversationThreadQuestions();
    public void handleConversationThreadAnswers();
    public void handleConversationThreadAnswerVotes();

}
