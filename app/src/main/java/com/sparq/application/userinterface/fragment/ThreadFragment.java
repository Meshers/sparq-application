package com.sparq.application.userinterface.fragment;

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
import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.almessage.AlQuestion;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.ConverstaionThreadActivity;
import com.sparq.application.userinterface.EventActivity;
import com.sparq.application.userinterface.adapter.QuizListAdapter;
import com.sparq.application.userinterface.adapter.RecyclerItemClickListener;
import com.sparq.application.userinterface.adapter.ThreadListAdapter;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.ConversationThread;
import com.sparq.application.userinterface.model.EventItem;
import com.sparq.application.userinterface.model.QuizItem;
import com.sparq.application.userinterface.model.UserItem;

import java.sql.Date;
import java.util.ArrayList;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;


public class ThreadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyBluetoothAdapter myBluetoothAdapter;
    private ApplicationLayerManager mApplicationLayerManager;
    private ApplicationPacketDiscoveryHandler handler;

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

//        initializeLowerLayer();

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

    public ConversationThread getConversationThread(int questionareId, int creatorId){


        for(ConversationThread thread: threadsArrayList){
            if(thread.getQuestionareId() == questionareId
                    && thread.getCreator().getUserId() == creatorId){
                return thread;
            }
        }
        return null;

    }

    public AnswerItem getAnswerForThread(ConversationThread thread, int answerId, int answerCreatorId){

        for(AnswerItem answer: thread.getAnswers()){

            if(answer.getAnswerId() == answerId
                    && thread.getCreator().getUserId() == answerCreatorId){
                return answer;
            }
        }
        return null;

    }

//    public void initializeLowerLayer(){
//        myBluetoothAdapter = new MyBluetoothAdapter(ConverstaionThreadActivity.this);
//
//        handler = new ApplicationPacketDiscoveryHandler() {
//            @Override
//            public void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage alMessage) {
//
//                ConversationThread retreivedThread;
//                AnswerItem retreivedAnswer;
//
//                switch(type){
//                    case QUESTION:
//
//                        AlQuestion alQuestion = (AlQuestion) alMessage;
//                        threadsArrayList.add(
//                                ConversationThread.getConversationThreadFromMessage(alQuestion)
//                        );
//
//                        break;
//                    case ANSWER:
//
//                        AlAnswer alAnswer = (AlAnswer) alMessage;
//                        retreivedThread = getConversationThread(alAnswer.getQuestionId(), alAnswer.getCreatorId());
//                        if(retreivedThread != null){
//                            retreivedThread.addAnswerToList(
//                                    AnswerItem.getAnswerItemFrommessage(alAnswer)
//                            );
//                        }
//
//                        break;
//                    case QUESTION_VOTE:
//
//                        AlVote questionVote = (AlVote) alMessage;
//
//                        retreivedThread = getConversationThread(questionVote.getQuestionId(), questionVote.getCreatorId());
//                        if(retreivedThread != null){
//
//                            switch(questionVote.getVoteValue()){
//                                case UPVOTE:
//                                    retreivedThread.getQuestionItem().addUpVote();
//                                    break;
//                                case DOWNVOTE:
//                                    retreivedThread.getQuestionItem().addDownVote();
//                                    break;
//                            }
//                        }
//
//                        break;
//                    case ANSWER_VOTE:
//
//                        AlVote answerVote = (AlVote) alMessage;
//
//                        retreivedThread = getConversationThread(answerVote.getQuestionId(), answerVote.getCreatorId());
//                        if(retreivedThread != null){
//
//                            retreivedAnswer = getAnswerForThread(retreivedThread, answerVote.getAnswerId(), answerVote.getAnswerCreatorId());
//                            if(retreivedAnswer != null){
//                                switch(answerVote.getVoteValue()){
//                                    case UPVOTE:
//                                        retreivedThread.getQuestionItem().addUpVote();
//                                        break;
//                                    case DOWNVOTE:
//                                        retreivedThread.getQuestionItem().addDownVote();
//                                        break;
//                                }
//                            }
//                        }
//
//                        break;
//
//                }
//            }
//        };
//
//        mApplicationLayerManager = new ApplicationLayerManager(ownAddr, myBluetoothAdapter, handler);
//
//    }

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
