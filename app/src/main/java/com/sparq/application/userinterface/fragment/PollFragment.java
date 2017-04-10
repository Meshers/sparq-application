package com.sparq.application.userinterface.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.userinterface.NotifyPollHandler;
import com.sparq.application.userinterface.adapter.DialogListAdapter;
import com.sparq.application.userinterface.adapter.PollListAdapter;
import com.sparq.application.userinterface.model.PollItem;

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
    private TextView emptyView;

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

        pollsArrayList = SPARQApplication.getPolls();

        if(pollsArrayList.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        initializePollAdapater();

        return view;
    }

    public void initializeView(View view){

        recyclerView = (RecyclerView) view.findViewById(R.id.poll_recycler_view);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
    }

    public void initializePollAdapater(){
        mAdapter = new PollListAdapter(getActivity(),pollsArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
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
                Toast.makeText(getActivity(), "Item Clicked: "+ itemIndex, Toast.LENGTH_SHORT).show();
                poll.setState(PollItem.getStateFromInteger(itemIndex));
                ImageView pollStatusImage = (ImageView) view.findViewById(R.id.poll_status);

                switch(poll.getState()){
                    case PLAY:
                        pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_play);
                        break;
                    case PAUSE:
                        pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_pause);
                        break;
                    case STOP:
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

    @Override
    public void onResume(){
        super.onResume();

        NotifyPollHandler pollHandler = new NotifyPollHandler() {
            @Override
            public void handlePollQuestions() {

                if(pollsArrayList.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                initializePollAdapater();
            }

            @Override
            public void handlePollAnswers() {
                // if an answer has arrived prevent the user from answering again
                initializePollAdapater();
            }
        };

        SPARQApplication.setPollNotifier(pollHandler);
    }

}
