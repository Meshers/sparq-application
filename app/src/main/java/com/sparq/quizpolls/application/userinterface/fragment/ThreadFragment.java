package com.sparq.quizpolls.application.userinterface.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.SPARQApplication;
import com.sparq.quizpolls.application.userinterface.NotifyThreadHandler;
import com.sparq.quizpolls.application.userinterface.adapter.ThreadListAdapter;
import com.sparq.quizpolls.application.userinterface.model.ConversationThread;
import com.sparq.quizpolls.application.userinterface.model.UserItem;
import com.sparq.quizpolls.util.Constants;

import java.sql.Date;
import java.util.ArrayList;

public class ThreadFragment extends Fragment {

    private static final String TAG = "ThreadFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<ConversationThread> threadsArrayList;
    private RecyclerView recyclerView;
    private ThreadListAdapter mAdapter;
    private TextView emptyView;

    private BroadcastReceiver timerReceiver;
    private boolean btnEnable;
    boolean isReceiverRegistered;

    public ThreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThreadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThreadFragment newInstance(String param1, String param2) {
        ThreadFragment fragment = new ThreadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_thread, container, false);

        initializeView(view);

        threadsArrayList = SPARQApplication.getConversationThreads();

        if(threadsArrayList.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        timerReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equalsIgnoreCase(Constants.UI_ENABLE_BROADCAST_INTENT)){
                    btnEnable = true;
                }
                else if(action.equalsIgnoreCase(Constants.UI_DISABLE_BROADCAST_INTENT)){
                    btnEnable = false;
                }

                // FIXME: 4/6/2017 I assume this redundancy is required?
                Log.i(TAG, "onReceive: timerReceiver");
                initializeThreadAdapter();

            }
        };

        Log.i(TAG, "onCreateView: afterTimerReceiver");
        initializeThreadAdapter();

        return view;
    }

    public void initializeThreadAdapter(){
        mAdapter = new ThreadListAdapter(threadsArrayList, getActivity().getApplicationContext(), btnEnable);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                initializeThreadAdapter();
            }
        });
    }

    public void initializeView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.thread_recycler_view);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public  void onResume(){
        super.onResume();

        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.UI_ENABLE_BROADCAST_INTENT);
            filter.addAction(Constants.UI_DISABLE_BROADCAST_INTENT);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getActivity().registerReceiver(timerReceiver,filter);
            isReceiverRegistered = true;
        }

        //Checks if the timer has elapsed, if it has the buttons can be active again
        if(SPARQApplication.isTimerElapsed()){
            Log.i(TAG, "OnResume: " + SPARQApplication.isTimerElapsed());
            btnEnable = true;
        }
        else {
            btnEnable = false;
        }

        initializeThreadAdapter();

        NotifyThreadHandler uiHandler = new NotifyThreadHandler() {
            @Override
            public void handleConversationThreadQuestions() {

                if(threadsArrayList.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void handleConversationThreadAnswers(){
                // do nothing
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void handleConversationThreadAnswerVotes(){
                // do nothing
            }
        };

        SPARQApplication.setThreadNotifier(uiHandler);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "onPause");

        if (isReceiverRegistered) {
            getActivity().unregisterReceiver(timerReceiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    public  void onStop(){
        super.onStop();
    }

    public ArrayList<ConversationThread> getData(){

        ArrayList<ConversationThread> threads = new ArrayList<ConversationThread>();

        UserItem user = new UserItem();

        for(int i = 0; i < 10; i++){

            ConversationThread thread = new ConversationThread(0, 1, new Date(2,3,2011), user, "How does this work?");

            threads.add(thread);
        }

        return threads;

    }

}
