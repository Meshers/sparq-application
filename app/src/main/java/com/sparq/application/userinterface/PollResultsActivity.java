package com.sparq.application.userinterface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.userinterface.adapter.ArrivedQuestionsAdapter;
import com.sparq.application.userinterface.adapter.QuestionAnswerListAdapter;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.Questionare;

public class PollResultsActivity extends AppCompatActivity {

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
                break;
            case POLL:
                questionare = SPARQApplication.getPoll(questionareId);
                break;
        }

        initializeViews();
    }

    public void initializeViews(){

        switch(type){
            case QUIZ:
                setTitle("Quiz Questions");
                break;
            case POLL:
                setTitle("Poll Questions");
                break;
        }

        questionListView = (RecyclerView) findViewById(R.id.question_recycler_view);

        mAdapter = new ArrivedQuestionsAdapter(PollResultsActivity.this, (PollItem) questionare);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PollResultsActivity.this);
        questionListView.setLayoutManager(mLayoutManager);
        questionListView.setItemAnimator(new DefaultItemAnimator());
        questionListView.setAdapter(mAdapter);

    }

}
