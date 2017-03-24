package com.sparq.application.userinterface.model;

import com.sparq.application.layer.almessage.AlQuestion;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sarahcs on 2/12/2017.
 */

public class ConversationThread extends Questionare {

    private static final int QUESTION_KEY = 1;

    private ArrayList<AnswerItem> answers;

    public ConversationThread(){
        super();
    }

    public ConversationThread(int threadId, int eventId, Date date, UserItem creator, String question){
        super(threadId, eventId, 2, null, null, date, creator, 1);

        QuestionItem questionItem = QuestionItem.getThreadQuestion(QUESTION_KEY, threadId, question);
        super.addQuestionToList(1, questionItem);

        answers = new ArrayList<AnswerItem>(0);

    }

    public QuestionItem getQuestionItem(){
        return super.getQuestionWithKey(QUESTION_KEY);
    }

    public String getQuestionString(){
        return super.getQuestionWithKey(QUESTION_KEY).getQuestion();
    }

    public ArrayList<AnswerItem> getAnswers(){
        return this.answers;
    }
    public void addAnswerToList(AnswerItem answer){
        this.answers.add(answer);
    }

    public AnswerItem getAnswer(int index){
        return this.answers.get(index);
    }

    public static   ConversationThread getConversationThreadFromMessage(AlQuestion alQuestion){
        return new ConversationThread(
                alQuestion.getQuestionId(),
                0, new Date(),
                new UserItem(alQuestion.getCreatorId()),
                alQuestion.getDataAsString()
        );
    }
}
