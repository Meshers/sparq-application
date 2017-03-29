package com.sparq.application.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.AnswerListAdapter;
import com.sparq.application.userinterface.adapter.RecyclerItemClickListener;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.ConversationThread;

import java.util.ArrayList;

public class ConverstaionThreadActivity extends AppCompatActivity {

    private TextView questionText;
    private EditText answerText;
    private Button postAnswer;
    private RecyclerView recyclerView;

    private int threadId, creatorId;
    private ConversationThread mThread;
    private ArrayList<AnswerItem> answersArrayList;
    private AnswerListAdapter mAdapter;

    public static final String THREAD_ID ="thread_id";
    public static final String CREATOR_ID ="creator_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converstaion_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            threadId = extras.getInt(THREAD_ID);
            creatorId = extras.getInt(CREATOR_ID);
        }

        mThread = SPARQApplication.getConversationThread(threadId, creatorId);

        answersArrayList = mThread.getAnswers();

        initializeViews();

        mAdapter = new AnswerListAdapter(answersArrayList);

        //TODO: shift the recyclerview below the edittext
        //Assuming you meant moving it in the display, it's done, if you had any other interpretation sorry
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int a =  (displaymetrics.heightPixels*45)/100;
        recyclerView.getLayoutParams().height = a;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(ConverstaionThreadActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {

                AnswerItem answer =  answersArrayList.get(position);

                Intent intent = new Intent(ConverstaionThreadActivity.this, AnswerActivity.class);

                intent.putExtra(AnswerActivity.THREAD_ID, threadId);
                intent.putExtra(AnswerActivity.CREATOR_ID, creatorId);
                intent.putExtra(AnswerActivity.ANSWER_ID, answer.getAnswerId());
                intent.putExtra(AnswerActivity.ANSWER_CREATOR_ID, answer.getCreator().getUserId());

//                Log.i(AnswerActivity.THREAD_ID, String.valueOf(threadId));
//                Log.i(AnswerActivity.CREATOR_ID, String.valueOf(creatorId));
//                Log.i(AnswerActivity.ANSWER_ID, String.valueOf(answer.getAnswerId()));
//                Log.i(AnswerActivity.ANSWER_CREATOR_ID, String.valueOf(answer.getCreator()));

                startActivity(intent);

            }
        }));
    }

    public void initializeViews(){

        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText(mThread.getQuestionItem().getQuestion());

        answerText = (EditText) findViewById(R.id.answer_text);
        postAnswer = (Button) findViewById(R.id.post_ans);
        recyclerView = (RecyclerView)  findViewById(R.id.answer_recycler_view);

        postAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SPARQApplication.sendMessage(
                        ApplicationLayerPdu.TYPE.ANSWER,
                        SPARQApplication.getBdcastAddress(),
                        answerText.getText().toString(),
                        creatorId,
                        threadId,
                        SPARQApplication.getOwnAddress(),
                        SPARQApplication.getCurrentAnswerId(),
                        null
                );
                Toast.makeText(ConverstaionThreadActivity.this, getResources().getString(R.string.ans_recorded), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public ArrayList<AnswerItem> getData(){
//
//        ArrayList<AnswerItem> answers = new ArrayList<AnswerItem>();
//
//        UserItem user = new UserItem();
//
//        for(int i = 0; i < 10; i++){
//
//            AnswerItem answer = new AnswerItem(i,new UserItem(),1,new UserItem(),"ANSWERRRRRRRRRRRRRRRRRRRRRRR", 0);
//
//            answers.add(answer);
//        }
//
//        return answers;
//
//    }

    @Override
    public void onResume(){
        super.onResume();

        NotifyUIHandler uiHandler = new NotifyUIHandler() {
            @Override
            public void handleConversationThreadQuestions() {
                // do nothing
            }

            @Override
            public void handleConversationThreadAnswers(){
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void handleConversationThreadAnswerVotes(){
                // do nothing
            }
        };

        SPARQApplication.setUINotifier(uiHandler);
    }

    @Override
    public void onStop(){
        super.onStop();
//        SPARQApplication.setUINotifier(null);
    }

}
