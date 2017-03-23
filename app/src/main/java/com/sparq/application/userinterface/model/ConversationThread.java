package com.sparq.application.userinterface.model;

import java.util.Date;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class ConversationThread extends Questionare {

    public ConversationThread(){
        super();
    }

    public ConversationThread(int threadId, int eventId, Date date, UserItem creator, String question){
        super(threadId, eventId, 2, null, null, date, creator, 1);

        QuestionItem questionItem = new QuestionItem(1, threadId, question,QuestionItem.FORMAT.SHORT, (double) 0);
        super.addQuestionToList(1, questionItem);

    }

    public QuestionItem getQuestionItem(int key){
        return super.getQuestionWithKey(key);
    }

    public String getQuestionString(int key){
        return super.getQuestionWithKey(key).getQuestion();
    }
}
