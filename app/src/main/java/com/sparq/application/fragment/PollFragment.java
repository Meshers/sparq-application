package com.sparq.application.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sparq.R;
import com.sparq.application.EventActivity;
import com.sparq.application.adapter.DialogListAdapter;
import com.sparq.application.adapter.PollListAdapter;
import com.sparq.application.adapter.QuizListAdapter;
import com.sparq.application.adapter.RecyclerItemClickListener;
import com.sparq.application.model.EventItem;
import com.sparq.application.model.PollItem;
import com.sparq.application.model.QuizItem;
import com.sparq.application.model.UserItem;

import java.sql.Date;
import java.util.ArrayList;

public class PollFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<PollItem> pollsArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PollListAdapter mAdapter;

    public PollFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PollFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PollFragment newInstance(String param1, String param2) {
        PollFragment fragment = new PollFragment();
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
        View view = inflater.inflate(R.layout.fragment_poll, container, false);

        initializeView(view);

        pollsArrayList = getData();

        mAdapter = new PollListAdapter(getActivity(),pollsArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {

                PollItem poll =  pollsArrayList.get(position);

                if(poll.getCreator().getUserType() == 0){
                    showStateDialog(poll, view);
                }
                else if(poll.getState() == 0){

                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }

            }
        }));

        return view;
    }

    public void initializeView(View view){

        recyclerView = (RecyclerView) view.findViewById(R.id.poll_recycler_view);
    }

    public ArrayList<PollItem> getData(){

        ArrayList<PollItem> polls = new ArrayList<PollItem>();

        UserItem user = new UserItem();

        for(int i = 0; i < 10; i++){

            PollItem poll = new PollItem(
                    i,
                    0,
                    "Poll "+i,
                    "this is a description",
                    new Date(2011,2,3),
                    0,
                    user);

            polls.add(poll);
        }

        return polls;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showStateDialog(final PollItem poll, final View view){

        ArrayList<String> options = new ArrayList<>();
        options.add("PLAY");
        options.add("PAUSE");
        options.add("STOP");

        int[] images = {
                R.drawable.ic_play,
                R.drawable.ic_pause,
                R.drawable.ic_stop
        };

        DialogListAdapter.ItemCallback itemCallback = new DialogListAdapter.ItemCallback() {
            @Override
            public void onItemClicked(int itemIndex, MaterialDialog dialog) {
                Toast.makeText(getActivity(), "Item Clicked:"+itemIndex, Toast.LENGTH_SHORT).show();
                poll.setState(itemIndex);
                ImageView pollStatusImage = (ImageView) view.findViewById(R.id.poll_status);

                switch(poll.getState()){
                    case 0:
                        pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_play);
                        break;
                    case 1:
                        pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_pause);
                        break;
                    case 2:
                        pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_stop);
                        break;
                }

                dialog.dismiss();

            }
        };


        final DialogListAdapter adapter = new DialogListAdapter(getActivity(), options, images);

        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Choose an Option")
                .adapter(adapter, null)
                .show();

        adapter.setCallbacks(itemCallback, dialog);

        RecyclerView list = dialog.getRecyclerView();
    }

}
