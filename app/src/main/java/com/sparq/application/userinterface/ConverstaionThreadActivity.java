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
import android.widget.ListView;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.AnswerListAdapter;
import com.sparq.application.userinterface.adapter.QuizListAdapter;
import com.sparq.application.userinterface.adapter.RecyclerItemClickListener;
import com.sparq.application.userinterface.adapter.ThreadListAdapter;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.ConversationThread;
import com.sparq.application.userinterface.model.QuizItem;
import com.sparq.application.userinterface.model.UserItem;

import java.sql.Date;
import java.util.ArrayList;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;
import test.com.blootoothtester.network.linklayer.LinkLayerManager;

import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE.ANSWER;
import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE.QUESTION;

public class ConverstaionThreadActivity extends AppCompatActivity {

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

        recyclerView = (RecyclerView)  findViewById(R.id.answer_recycler_view);

        mThread = SPARQApplication.getConversationThread(threadId, creatorId);

        answersArrayList = mThread.getAnswers();

        mAdapter = new AnswerListAdapter(answersArrayList);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int a =  (displaymetrics.heightPixels*45)/100;
        recyclerView.getLayoutParams().height =a;

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
                intent.putExtra(AnswerActivity.ANSWER_CREATOR_ID, answer.getCreator());

                startActivity(intent);

            }
        }));
    }



    public ArrayList<AnswerItem> getData(){

        ArrayList<AnswerItem> answers = new ArrayList<AnswerItem>();

        UserItem user = new UserItem();

        for(int i = 0; i < 10; i++){

            AnswerItem answer = new AnswerItem(i,new UserItem(),1,new UserItem(),"ANSWERRRRRRRRRRRRRRRRRRRRRRR", 0);

            answers.add(answer);
        }

        return answers;

    }

}
