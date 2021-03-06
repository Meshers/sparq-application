package com.sparq.application;

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
import com.sparq.application.adapter.AnswerListAdapter;
import com.sparq.application.adapter.QuizListAdapter;
import com.sparq.application.adapter.RecyclerItemClickListener;
import com.sparq.application.adapter.ThreadListAdapter;
import com.sparq.application.model.AnswerItem;
import com.sparq.application.model.ConversationThread;
import com.sparq.application.model.QuizItem;
import com.sparq.application.model.UserItem;

import java.sql.Date;
import java.util.ArrayList;

public class ConverstaionThreadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<AnswerItem> answersArrayList;
    private AnswerListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converstaion_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)  findViewById(R.id.answer_recycler_view);

        answersArrayList = getData();

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

                intent.putExtra("Answer", answer);
                startActivity(intent);

            }
        }));
    }

    public ArrayList<AnswerItem> getData(){

        ArrayList<AnswerItem> answers = new ArrayList<AnswerItem>();

        UserItem user = new UserItem();

        for(int i = 0; i < 10; i++){

            AnswerItem answer = new AnswerItem(i, 2, "In our previous post we had talking about the \"Experiment that decides all\". This post talks about the results of the various tests conducted as part of the experiment. If you wish to read about the experiment in detail please refer to our previous post.", user);

            answers.add(answer);
        }

        return answers;

    }

}
