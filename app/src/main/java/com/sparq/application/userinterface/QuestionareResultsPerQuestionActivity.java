package com.sparq.application.userinterface;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.userinterface.adapter.AnswerListAdapter;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.Questionare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class QuestionareResultsPerQuestionActivity extends AppCompatActivity {

    public static final String QUESTION_FORMAT = "question_format";
    public static final String QUESTIONARE_ID = "questionare_id";
    public static final String QUESTION_ID = "question_id";
    public static final String QUESTIONARE_TYPE = "questionare_type";
    public static final String POLL_NAME = "poll_name";

    private QuestionItem.FORMAT format;
    private int questionareId, questionId;
    private Questionare.QUESTIONARE_TYPE type;
    private String questionareName = "Questions";

    private TextView questionText;
    private RecyclerView answerRecyclerView;
    private CardView graphCard, recyclerCard;
    private GraphView graph;

    private QuestionItem question;
    private ArrayList<AnswerItem> answers;
    private AnswerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_results_per_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            type = (Questionare.QUESTIONARE_TYPE) bundle.getSerializable(QUESTIONARE_TYPE);
            format = (QuestionItem.FORMAT) bundle.getSerializable(QUESTION_FORMAT);
            questionareId = bundle.getInt(QUESTIONARE_ID);
            questionId = bundle.getInt(QUESTION_ID);
            Log.i("onCreate: ", questionareName);
        }



        switch(type){
            case QUIZ:
                question = SPARQApplication.getQuiz(questionareId).getQuestionWithKey(questionId);
                answers = SPARQApplication.getQuiz(questionareId).getAnswersForQuestion(questionId);
                questionareName = SPARQApplication.getQuiz(questionareId).getName();
                toolbar.setTitle(questionareName);
                break;
            case POLL:
                question = SPARQApplication.getPoll(questionareId).getQuestionWithKey(questionId);
                answers = SPARQApplication.getPoll(questionareId).getAnswersForQuestion(questionId);
                questionareName = SPARQApplication.getPoll(questionareId).getName();
                toolbar.setTitle(questionareName);
        }
        initializeViews();

    }

    public void initializeViews(){

        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText(question.getQuestion());

        answerRecyclerView = (RecyclerView) findViewById(R.id.answer_recycler_view);
        graphCard = (CardView) findViewById(R.id.card_view1);
        recyclerCard = (CardView) findViewById(R.id.card_view2);
        graph = (GraphView) findViewById(R.id.graph);

        switch(format){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                graphCard.setVisibility(View.VISIBLE);
                recyclerCard.setVisibility(View.GONE);
                initializeGraph();
                break;
            case ONE_WORD:
            case SHORT:
                recyclerCard.setVisibility(View.VISIBLE);
                graphCard.setVisibility(View.GONE);
                break;
        }

        mAdapter = new AnswerListAdapter(answers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        answerRecyclerView.setLayoutManager(mLayoutManager);
        answerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        answerRecyclerView.setAdapter(mAdapter);

    }

    public void initializeGraph(){

        TreeMap<Integer, Integer> data = new TreeMap<>();
        for(AnswerItem answer: answers){
            ArrayList<Integer> answerChoices = answer.getAnswerChoices();

            Log.i("HEREeeee", answerChoices.toString());
            for(int answerChoice: answerChoices){
                if(data.containsKey(answerChoice)){
                    data.put(answerChoice, data.get(answerChoice)+ 1);
                }
                else{
                    data.put(answerChoice, 1);
                }
            }

        }

        Log.i("HERE", data.toString());

        DataPoint[] dataPoints = new DataPoint[data.size()+1];
        dataPoints[0] = new DataPoint(0,0);

        int i = 1;
        for(int key: data.keySet()){
            dataPoints[i] = new DataPoint(key, data.get(key));
            i++;
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
        graph.addSeries(series);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(1);

        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.GRAY);
    }

}
