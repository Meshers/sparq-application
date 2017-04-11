package com.sparq.application.userinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.userinterface.adapter.ArrivedQuestionsAdapter;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.Questionare;
import com.sparq.application.userinterface.model.QuizItem;

public class QuestionareResultsActivity extends AppCompatActivity {

    public static final String QUESTIONARE_ID = "questionare_id";
    public static final String QUESTIONARE_TYPE = "questionare_type";

    private int questionareId;
    private Questionare.QUESTIONARE_TYPE type;
    private Questionare questionare;
    private ArrivedQuestionsAdapter mAdapter;

    private RecyclerView questionListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            questionareId = bundle.getInt(QUESTIONARE_ID);
            type = (Questionare.QUESTIONARE_TYPE) bundle.getSerializable(QUESTIONARE_TYPE);
        }

        switch(type){
            case QUIZ:
                questionare = SPARQApplication.getQuiz(questionareId);
                break;
            case POLL:
                questionare = SPARQApplication.getPoll(questionareId);
                break;
        }

        initializeViews();
    }

    public void initializeViews(){

        questionListView = (RecyclerView) findViewById(R.id.question_recycler_view);

        switch(type){
            case QUIZ:
                setTitle("Quiz Questions");
                mAdapter = new ArrivedQuestionsAdapter(QuestionareResultsActivity.this, Questionare.QUESTIONARE_TYPE.QUIZ, questionare);

                break;
            case POLL:
                setTitle("Poll Questions");
                mAdapter = new ArrivedQuestionsAdapter(QuestionareResultsActivity.this, Questionare.QUESTIONARE_TYPE.POLL, questionare);

                break;
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(QuestionareResultsActivity.this);
        questionListView.setLayoutManager(mLayoutManager);
        questionListView.setItemAnimator(new DefaultItemAnimator());
        questionListView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                initializeViews();
            }
        });

    }

}
