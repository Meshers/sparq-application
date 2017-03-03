package com.sparq.application.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sparq.R;
import com.sparq.application.ConverstaionThreadActivity;
import com.sparq.application.EventActivity;
import com.sparq.application.adapter.QuizListAdapter;
import com.sparq.application.adapter.RecyclerItemClickListener;
import com.sparq.application.adapter.ThreadListAdapter;
import com.sparq.application.model.ConversationThread;
import com.sparq.application.model.EventItem;
import com.sparq.application.model.QuizItem;
import com.sparq.application.model.UserItem;

import java.sql.Date;
import java.util.ArrayList;


public class ThreadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<ConversationThread> threadsArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ThreadListAdapter mAdapter;

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

        threadsArrayList = getData();

        mAdapter = new ThreadListAdapter(threadsArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {

                ConversationThread thread =  threadsArrayList.get(position);

                Intent intent = new Intent(getActivity(), ConverstaionThreadActivity.class);
                intent.putExtra("event_id", thread.getQuestionareId());
                startActivity(intent);

            }
        }));


        return view;
    }

    public void initializeView(View view){

        recyclerView = (RecyclerView) view.findViewById(R.id.thread_recycler_view);
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
