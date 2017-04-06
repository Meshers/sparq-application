package com.sparq.application.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.AnswerListAdapter;
import com.sparq.application.userinterface.adapter.QuestionAnswerListAdapter;
import com.sparq.application.userinterface.adapter.QuestionareAdapter;
import com.sparq.application.userinterface.adapter.RecyclerItemClickListener;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.Questionare;
import com.sparq.application.userinterface.model.QuizItem;
import com.sparq.application.userinterface.model.UserItem;
import com.sparq.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class QuestionareActivity extends AppCompatActivity {

    private TextView timer;
    private RecyclerView questionView;
    private com.getbase.floatingactionbutton.FloatingActionButton submitAnswers;


    private Questionare questionare;
    private HashMap<Integer, QuestionItem> questions;
    private QuestionAnswerListAdapter mAdapter;
    private int questionareId;
    private Questionare.QUESTIONARE_TYPE type;

    public static final String QUESTIONARE_TYPE = "questionare_type";
    public static final String QUESTIONARE_ID = "questionare_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionare);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            questionareId = bundle.getInt(QUESTIONARE_ID);
            type = (Questionare.QUESTIONARE_TYPE) bundle.getSerializable(QUESTIONARE_TYPE);
        }

        timer = (TextView) findViewById(R.id.count_down_timer);


        // get the quiz / poll object
        questionare = getData(type);

        // get questions related to a particular quiz or poll
        questions = questionare.getQuestions();

        initializeViews();

        switch(questionare.getType()){
            case QUIZ:
                // QUIZ
                callTimer();
                break;
            case POLL:
                // POLL
                ((LinearLayout)timer.getParent()).removeView(timer);
                break;
        }


    }

    public void initializeViews(){


        questionView = (RecyclerView) findViewById(R.id.question_recycler_view);
        submitAnswers = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.submit_answers);

        ArrayList<QuestionItem> questionsArray = new ArrayList<>(questions.values());
        mAdapter = new QuestionAnswerListAdapter(QuestionareActivity.this, questionsArray);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(QuestionareActivity.this);
        questionView.setLayoutManager(mLayoutManager);
        questionView.setItemAnimator(new DefaultItemAnimator());
        questionView.setAdapter(mAdapter);

        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendPollMessage( mAdapter.getAnswerForQuestion());
                finish();

//                ((PollItem) questionare).addAnswerArrayToQuestion(questionareId, answersArray);
            }
        });

    }

    public Questionare getData(Questionare.QUESTIONARE_TYPE type){


        UserItem user = new UserItem();

        switch (type){
            case QUIZ:
//                QuizItem quiz = new QuizItem(1,1,"Quiz 0", "blah blah", new Date(11,12,2011), 2, 1, 50, user);
//                HashMap<Integer, String> options1 = new HashMap<Integer, String>();
//                options1.put(0, "YES");
//                options1.put(1, "NO");
//                quiz.addQuestionToList(
//                        new QuestionItem(0, 1, "Do you like the app?", 1, , options1, Constants.INITIAL_VOTE_COUNT)
//                );
//
                return null;
            case POLL:

                return SPARQApplication.getPoll(questionareId);
        }

        return null;
    }

    public void callTimer(){
        //set a timer here

    }

    public void sendPollMessage(ArrayList<AnswerItem> answers){

        /**
         * TODO: set a timer to call the sendPoll from NewQuestionareActivity for each question. the timer should have a gap of 12s.
         * alternatively u can change it in SPARQApplication
         */

        for(AnswerItem answer: answers){

            String answerMessage = null;
            switch(answer.getFormat()){
                case MCQ_SINGLE:
                case MCQ_MULTIPLE:
                    answerMessage = answer.getAnswerChoicesAsString();
                    break;
                case ONE_WORD:
                case SHORT:
                    answerMessage= answer.getAnswer();
                    break;
            }

            SPARQApplication.sendPollMessage(
                    ApplicationLayerPdu.TYPE.POLL_ANSWER,
                    SPARQApplication.getBdcastAddress(),
                    answerMessage,
                    questionareId,
                    (int) SPARQApplication.getOwnAddress(),
                    answer.getQuestionItemId(),
                    answer.getFormat(),
                    null,
                    SPARQApplication.getOwnAddress(),
                    false,
                    false,
                    false
            );

        }
    }

}
