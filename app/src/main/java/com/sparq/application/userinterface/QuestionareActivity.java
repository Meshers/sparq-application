package com.sparq.application.userinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.QuestionAnswerListAdapter;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.Questionare;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionareActivity extends AppCompatActivity {

    private TextView timer;
    private RecyclerView questionView;
    private com.getbase.floatingactionbutton.FloatingActionButton submitAnswers;


    private Questionare questionare;
    private HashMap<Integer, QuestionItem> questions;
    private QuestionAnswerListAdapter mAdapter;
    private int questionareId;
    private int questionCreatorId;
    private Questionare.QUESTIONARE_TYPE type;

    private HashMap<Integer, String> bundledMessage = new HashMap<>(0);
    private QuestionItem.FORMAT quizFormat;

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
        //initialize the question creator
        questionCreatorId = questionare.getCreator().getUserId();

        // get questions related to a particular quiz or poll
        questions = questionare.getQuestions();

        initializeViews();

        switch(questionare.getType()){
            case QUIZ:
                // QUIZ
//                callTimer();
//                break;
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

                switch(type){
                    case QUIZ:
                        if(sendQuizMessage( mAdapter.getAnswerForQuestion())){
                            finish();
                            EventActivity.mEventActivity.showBtResponseProgressDialog();
                        }
                        break;
                    case POLL:
                        if(sendPollMessage( mAdapter.getAnswerForQuestion())){
                            finish();
                            EventActivity.mEventActivity.showBtResponseProgressDialog();
                        }
                        break;
                }

            }
        });

    }

    public Questionare getData(Questionare.QUESTIONARE_TYPE type){

        switch (type){
            case QUIZ:

                return SPARQApplication.getQuiz(questionareId);
            case POLL:

                return SPARQApplication.getPoll(questionareId);
        }

        return null;
    }

    public void callTimer(){
        //set a timer here

    }

    public void bundleAnswers(AnswerItem answer){

        quizFormat = answer.getFormat();

        String answerMessage = "";
        switch(answer.getFormat()){
            case MCQ_SINGLE:
            case MCQ_MULTIPLE:
                answerMessage = answer.getAnswerChoicesAsString();
                answerMessage += "#";
                break;
            case ONE_WORD:
            case SHORT:
                answerMessage= answer.getAnswer();
                break;
        }
        Log.i("HERE", answerMessage);
        bundledMessage.put(answer.getAnswerId(), answerMessage);
    }

    public boolean sendQuizMessage(ArrayList<AnswerItem> answers){

        for(AnswerItem answer: answers){
            if(answer.getAnswerChoices().isEmpty()){
                Toast.makeText(QuestionareActivity.this, "Please ensure that all questions are answered",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            bundleAnswers(answer);
        }

        Log.i("HERE", bundledMessage.toString());

        SPARQApplication.sendQuizMessage(
                ApplicationLayerPdu.TYPE.QUIZ_ANSWER,
                SPARQApplication.getBdcastAddress(),
                bundledMessage,
                questionare.getQuestionareId(),
                questionCreatorId,
                quizFormat,
                bundledMessage.size(),
                null,
                SPARQApplication.getOwnAddress(),
                null,
                null
        );
        return true;
    }

//    public void sendPollMessage(ArrayList<AnswerItem> answers){
//
//        for(AnswerItem answer: answers){
//
//            String answerMessage = null;
//            switch(answer.getFormat()){
//                case MCQ_SINGLE:
//                case MCQ_MULTIPLE:
//                    answerMessage = answer.getAnswerChoicesAsString();
//                    break;
//                case ONE_WORD:
//                case SHORT:
//                    answerMessage= answer.getAnswer();
//                    break;
//            }
//
//            SPARQApplication.sendPollMessage(
//                    ApplicationLayerPdu.TYPE.POLL_ANSWER,
//                    SPARQApplication.getBdcastAddress(),
//                    answerMessage,
//                    questionareId,
//                    questionCreatorId,
//                    answer.getQuestionItemId(),
//                    answer.getFormat(),
//                    null,
//                    SPARQApplication.getOwnAddress(),
//                    false,
//                    false,
//                    false
//            );
//
//        }
//    }

    public boolean sendPollMessage(ArrayList<AnswerItem> answers){

        for(AnswerItem answer: answers){
            if(answer.getAnswerChoices().isEmpty()){
                Toast.makeText(QuestionareActivity.this, "Please Ensure that all questions are answered",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            bundleAnswers(answer);
        }

        SPARQApplication.sendPollMessage(
                ApplicationLayerPdu.TYPE.POLL_ANSWER,
                SPARQApplication.getBdcastAddress(),
                bundledMessage,
                questionare.getQuestionareId(),
                questionCreatorId,
                quizFormat,
                bundledMessage.size(),
                null,
                SPARQApplication.getOwnAddress()
        );
        return true;
    }

}
