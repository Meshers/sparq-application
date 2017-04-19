package com.sparq.quizpolls.application.userinterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.SPARQApplication;
import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.quizpolls.application.userinterface.adapter.AnswerListAdapter;
import com.sparq.quizpolls.application.userinterface.adapter.RecyclerItemClickListener;
import com.sparq.quizpolls.application.userinterface.model.AnswerItem;
import com.sparq.quizpolls.application.userinterface.model.ConversationThread;
import com.sparq.quizpolls.util.Constants;

import java.util.ArrayList;

import static com.sparq.quizpolls.application.SPARQApplication.getInstance;

public class ConverstaionThreadActivity extends AppCompatActivity {

    private static final String TAG = "CTActivity";
    private TextView questionText;
    private TextView answerCount;
    private TextView emptyView;
    private EditText answerText;
    private Button postAnswer;
    private RecyclerView recyclerView;

    private int threadId, creatorId;
    private ConversationThread mThread;
    private ArrayList<AnswerItem> answersArrayList;
    private AnswerListAdapter mAdapter;

    private BroadcastReceiver timerReceiver;
    boolean isReceiverRegistered;

    public static final String THREAD_ID ="thread_id";
    public static final String CREATOR_ID ="creator_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converstaion_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            threadId = extras.getInt(THREAD_ID);
            creatorId = extras.getInt(CREATOR_ID);
        }

        mThread = SPARQApplication.getConversationThread(threadId, creatorId);

        answersArrayList = mThread.getAnswers();

        initializeViews();

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

        timerReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equalsIgnoreCase(Constants.UI_ENABLE_BROADCAST_INTENT)){
                    postAnswer.setEnabled(true);
                }
                else if(action.equalsIgnoreCase(Constants.UI_DISABLE_BROADCAST_INTENT)){
                    postAnswer.setEnabled(false);
                }

            }
        };
    }

    public void initializeViews(){

        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText(mThread.getQuestionItem().getQuestion());

        answerCount = (TextView) findViewById(R.id.answer_count);
        answerCount.setText(String.valueOf(mThread.getAnswers().size()));

        answerText = (EditText) findViewById(R.id.answer_text);
        postAnswer = (Button) findViewById(R.id.post_ans);
        recyclerView = (RecyclerView)  findViewById(R.id.answer_recycler_view);

        emptyView = (TextView) findViewById(R.id.empty_view);
        if(mThread.getAnswers().size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        postAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerText.getText().toString().length() == 0){
                    Toast.makeText(ConverstaionThreadActivity.this, getResources().getString(R.string.empty_field_msg), Toast.LENGTH_SHORT).show();
                }
                else{
                    SPARQApplication.sendThreadMessage(
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
                    //Re-start the timer to disable buttons
                    getInstance().startTimer();
                    answerText.setText("");

                    if(mAdapter != null){
                        mAdapter.notifyDataSetChanged();

                        if(mThread.getAnswers().size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        recyclerView.getLayoutParams().height = (int)((displaymetrics.heightPixels*45)/100);

        mAdapter = new AnswerListAdapter(answersArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
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

        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.UI_ENABLE_BROADCAST_INTENT);
            filter.addAction(Constants.UI_DISABLE_BROADCAST_INTENT);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(timerReceiver,filter);
            isReceiverRegistered = true;
        }

        //Checks if the timer has elapsed, if it has the buttons can be active again
        if(SPARQApplication.isTimerElapsed()){
            postAnswer.setEnabled(true);
        }
        else {
            postAnswer.setEnabled(false);
        }

        NotifyThreadHandler uiHandler = new NotifyThreadHandler() {
            @Override
            public void handleConversationThreadQuestions() {
                // do nothing
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void handleConversationThreadAnswers(){
                mAdapter.notifyDataSetChanged();
                answerCount.setText(String.valueOf(mThread.getAnswers().size()));
            }

            @Override
            public void handleConversationThreadAnswerVotes(){
                // do nothing
            }
        };

        SPARQApplication.setThreadNotifier(uiHandler);
    }


    @Override
    public void onPause(){
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(timerReceiver);
            isReceiverRegistered = false;
        }
    }

    // FIXME: 4/3/2017
    @Override
    public void onStop(){
        super.onStop();
//        SPARQApplication.setThreadNotifier(null);
    }

}
