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
import com.sparq.application.userinterface.adapter.AnswerListAdapter;
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
    private TextView questionText;
    private View layoutShortAns;
    private RecyclerView recyclerView;
    private TextView answerText;

    private Questionare questionare;
    private HashMap<Integer, QuestionItem> questions;
    private QuestionareAdapter mAdapter;
    private Questionare.QUESTIONARE_TYPE type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionare);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        type = (Questionare.QUESTIONARE_TYPE) bundle.getSerializable("type");

        timer = (TextView) findViewById(R.id.count_down_timer);
        questionText = (TextView) findViewById(R.id.question_text);
        layoutShortAns = (View) findViewById(R.id.layout_short_ans);
        recyclerView = (RecyclerView) findViewById(R.id.questionare_recycler_view);
        answerText = (TextView) findViewById(R.id.answer_text);

        // get the quiz / poll object
        questionare = getData(type);

        // get questions related to a perticular quiz or poll
        questions = questionare.getQuestions();

        mAdapter = new QuestionareAdapter(
                new ArrayList<String>(questionare.getQuestions().get(0).getOptions().values()), questionare.getType()
        );

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int a =  (displaymetrics.heightPixels*45)/100;
        recyclerView.getLayoutParams().height =a;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(QuestionareActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {


            }
        }));

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
//                return quiz;
            case POLL:
//                PollItem poll = new PollItem(1,1,"Quiz 0", "blah blah", new Date(11,12,2011), 2, user);
//                HashMap<Integer, String> options2 = new HashMap<Integer, String>();
//                options2.put(0, "YES");
//                options2.put(1, "NO");
//                poll.addQuestionToList(0, 1, "Do you like the app?", 1, 1, options2, Constants.INITIAL_VOTE_COUNT);
//
//                return poll;
        }

        return null;
    }

    public void callTimer(){
        //set a timer here

    }

}
