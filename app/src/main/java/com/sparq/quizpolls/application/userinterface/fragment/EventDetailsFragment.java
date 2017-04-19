package com.sparq.quizpolls.application.userinterface.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sparq.quizpolls.R;

public class EventDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView eventCodeText;
    TextView agendaText;
    TextView venueText;
    TextView dateText;
    TextView durationText;
    TextView usernameText;
    TextView phoneText;
    TextView emailText;


    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        initializeViews(view);

        return view;
    }

    public void initializeViews(View v){

        eventCodeText = (TextView) v.findViewById(R.id.event_code_text);
        agendaText = (TextView) v.findViewById(R.id.agenda_text);
        venueText = (TextView) v.findViewById(R.id.venue_text);
        dateText = (TextView) v.findViewById(R.id.date_time_text);
        durationText = (TextView) v.findViewById(R.id.duration_text);
        usernameText = (TextView) v.findViewById(R.id.username_text);
        phoneText = (TextView) v.findViewById(R.id.phone_text);
        emailText = (TextView) v.findViewById(R.id.email_text);
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
