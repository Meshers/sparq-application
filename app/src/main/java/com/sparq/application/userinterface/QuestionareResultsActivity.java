package com.sparq.application.userinterface;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.userinterface.adapter.ArrivedQuestionsAdapter;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.Questionare;
import com.sparq.application.userinterface.model.QuizItem;
import com.sparq.util.ResultsLogger;

import java.util.HashMap;

public class QuestionareResultsActivity extends AppCompatActivity {

    public static final String QUESTIONARE_ID = "questionare_id";
    public static final String QUESTIONARE_TYPE = "questionare_type";

    private int questionareId;
    private Questionare.QUESTIONARE_TYPE type;
    private Questionare questionare;
    private ArrivedQuestionsAdapter mAdapter;

    private RecyclerView questionListView;
    private Button csvButton;

    private ResultsLogger resultsLogger;
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

        resultsLogger = new ResultsLogger(questionare.getName());
        initializeViews();
    }

    public void initializeViews(){

        questionListView = (RecyclerView) findViewById(R.id.question_recycler_view);
        csvButton = (Button) findViewById(R.id.csv_export);

        csvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportResultsToCSV();
            }
        });

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

    }

    public void exportResultsToCSV(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if(resultsLogger.writeResults(((QuizItem) questionare).getUserScores())){

                Toast.makeText(QuestionareResultsActivity.this, "Successfully written to a CSV file",
                        Toast.LENGTH_SHORT).show();
            }else{

                Toast.makeText(QuestionareResultsActivity.this, "Failed to write to CSV file",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}
