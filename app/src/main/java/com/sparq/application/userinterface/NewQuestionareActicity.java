package com.sparq.application.userinterface;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.OptionsAdapter;
import com.sparq.application.userinterface.adapter.PollListAdapter;
import com.sparq.application.userinterface.adapter.QuestionAdapter;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.UserItem;
import com.sparq.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NewQuestionareActicity extends AppCompatActivity {

    private TextView questionareNameText;
//    private EditText descriptionText;
    private EditText durationText;
    private FloatingActionButton addQuestionare;
    private FloatingActionButton newQuestion;
    private RecyclerView questionsRecyclerView;

    private QuestionAdapter mQuestionAdapter;

    PollItem newPoll;
    private HashMap<Integer, QuestionItem> questionsArray = new HashMap<>(0);

    public static final String QUESTIONARE_TYPE = "questionare_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_questionare_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get bundle
        newPoll = new PollItem(
                SPARQApplication.getPolls().size()+1,
                SPARQApplication.getSessionId(),
                null, null,
                new Date(),
                PollItem.POLL_STATE.STOP,
                new UserItem(SPARQApplication.getOwnAddress())
        );

        initializeViews();

    }

    public void initializeViews(){

        // TODO: in the view, remove the "edit option" over the blue area. Relpace the textview in the collapsable layout with a toolbar as present in activity_questionare.xml_

        newQuestion = (FloatingActionButton) findViewById(R.id.fab_new_questonare);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewQuestionDalog();
            }
        });

//        questionareNameText = (TextView) findViewById(R.id.edit_questionare_name);
        durationText = (EditText) findViewById(R.id.duration_text);
        questionsRecyclerView = (RecyclerView) findViewById(R.id.questionare_recycler_view);
        addQuestionare = (FloatingActionButton) findViewById(R.id.add_questionare);

        addQuestionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add all the questions to the questionare object
                newPoll.setQuestions(questionsArray);
                sendPollMessage(
                        ApplicationLayerPdu.TYPE.POLL_QUESTION,
                        SPARQApplication.getBdcastAddress(),
                        newPoll
                );

                finish();
            }
        });

        mQuestionAdapter = new QuestionAdapter(questionsArray);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewQuestionareActicity.this);
        questionsRecyclerView.setLayoutManager(mLayoutManager);
        questionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        questionsRecyclerView.setAdapter(mQuestionAdapter);
    }

    public void openNewQuestionDalog(){

        // TODO: donot allow users leave options blank if they choose MCQs. Atleast 1 option should exist

        final String[] SPINNERLIST = {
                "Single Choice MCQ",
                "Multiple Choice MCQ",
                "One Word Answers",
                "Short Answers"
        };

        final ArrayList<String> options = new ArrayList<>();

        final QuestionItem.FORMAT format[] = new QuestionItem.FORMAT[1];
        format[0] = QuestionItem.getFormatFromByte((byte) 1);
        final OptionsAdapter mAdapter = new OptionsAdapter(options);
        final boolean[] disableButton = new boolean[1];

        MaterialDialog dialog = new MaterialDialog.Builder(NewQuestionareActicity.this)
                .title("Add a New Question")
                .customView(R.layout.dialog_new_question, true)
                .positiveText("ADD")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        View view = dialog.getCustomView();
                        EditText questionName = (EditText) view.findViewById(R.id.question_text);
                        SwitchCompat mainQuestion = (SwitchCompat) view.findViewById(R.id.switchButton);

                        if(mAdapter.getItemCount() < 2){

                            disableButton[0] = true;
                            // FIXME: 4/7/2017 make the options dialog stay with the toast use disableButton variable
                            Toast.makeText(NewQuestionareActicity.this, getResources().getString(R.string.more_options),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            disableButton[0] = false;
                            QuestionItem newQuestion = new QuestionItem(
                                    questionsArray.size()+1,
                                    newPoll.getQuestionareId(),
                                    questionName.getText().toString(),
                                    format[0],
                                    Constants.MIN_QUESTION_MARKS,
                                    mAdapter.getOptions(),
                                    Constants.INITIAL_VOTE_COUNT
                            );
                            questionsArray.put(questionsArray.size()+1, newQuestion);

                            //notify dataset changed
                            mQuestionAdapter.notifyDataSetChanged();

                            if(mainQuestion.isChecked()){
                                newQuestion.setMainQuestion(true);
                                newPoll.setName(questionName.getText().toString());
                            }
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();

        View view = dialog.getCustomView();

        final LinearLayout hideLayout = (LinearLayout) view.findViewById(R.id.hideable_layout);

        final RecyclerView optionsListView = (RecyclerView) view.findViewById(R.id.options_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewQuestionareActicity.this);
        optionsListView.setLayoutManager(mLayoutManager);
        optionsListView.setItemAnimator(new DefaultItemAnimator());
        optionsListView.setAdapter(mAdapter);

        final MaterialSpinner formatSpinner = (MaterialSpinner) view.findViewById(R.id.format_spinner);
        formatSpinner.setItems(SPINNERLIST);

        formatSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item.equalsIgnoreCase(SPINNERLIST[0])){
                    format[0] = QuestionItem.getFormatFromByte((byte) 1);
                    hideLayout.setVisibility(View.VISIBLE);

                } else if(item.equalsIgnoreCase(SPINNERLIST[1])){
                    format[0] = QuestionItem.getFormatFromByte((byte) 2);
                    hideLayout.setVisibility(View.VISIBLE);

                }else if(item.equalsIgnoreCase(SPINNERLIST[2])){
                    format[0] = QuestionItem.getFormatFromByte((byte) 3);
                    hideLayout.setVisibility(View.GONE);

                } else if(item.equalsIgnoreCase(SPINNERLIST[3])){
                    format[0] = QuestionItem.getFormatFromByte((byte) 4);
                    hideLayout.setVisibility(View.GONE);
                }
                Log.i("format", item);
            }

        });


        final EditText option = (EditText) view.findViewById(R.id.option_text);
        ImageView addOption = (ImageView) view.findViewById(R.id.add_option);

        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FIXME: 4/7/2017 Disallow placeholder text be an option
                if(option.getText().toString().compareTo(getResources().getString(R.string.enter_option)) == 0){
                    options.add(option.getText().toString());
                    mAdapter.notifyDataSetChanged();
                    option.setText("");
                }

            }
        });

        if(disableButton[0] == true){
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
        else{
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        }
        dialog.show();
    }

    public void sendPollMessage(final ApplicationLayerPdu.TYPE type, final byte toAddr, final PollItem poll){

        /**
         * TODO: set a timer to call the sendPoll from NewQuestionareActivity for each question. the timer should have a gap of 12s.
         * alternatively u can change it in SPARQApplication
         * I really have no clue how to deal with this, I tired handlers and shit, didn't work
         */

        /*Handler mHandler = new Handler();
        // FIXME: 4/7/2017 Change this to constants
        final int delay = 12000;

        final ArrayList<QuestionItem> questions = new ArrayList<>(poll.getQuestions().values());
        int i = 0;
        for(; i < questions.size() - 1; i++){

            final QuestionItem question = questions.get(i);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SPARQApplication.sendPollMessage(
                            type,
                            toAddr,
                            question.getQuestion(),
                            poll.getQuestionareId(),
                            (int) SPARQApplication.getOwnAddress(),
                            question.getQuestionId(),
                            question.getFormat(),
                            question.getOptions(),
                            0,
                            true,
                            false,
                            question.isMainQuestion()
                    );
                }
            }, delay);*/

        final ArrayList<QuestionItem> questions = new ArrayList<>(poll.getQuestions().values());
        int i = 0;
        for(; i < questions.size() - 1; i++) {

            QuestionItem question = questions.get(i);
            SPARQApplication.sendPollMessage(
                    type,
                    toAddr,
                    question.getQuestion(),
                    poll.getQuestionareId(),
                    (int) SPARQApplication.getOwnAddress(),
                    question.getQuestionId(),
                    question.getFormat(),
                    question.getOptions(),
                    0,
                    true,
                    false,
                    question.isMainQuestion()
            );

        }

        QuestionItem question = questions.get(i);
//        Log.i("HERE", "outside loop");
//        Log.i("HERE",
//                question.getQuestion() + ":"+
//                        poll.getQuestionareId() + ":"+
//                        (int) SPARQApplication.getOwnAddress() + ":"+
//                        question.getQuestionId() + ":"+
//                        question.getFormat() + ":"+
//                        question.getOptions() + ":"+
//                        question.isMainQuestion()
//        );

        SPARQApplication.sendPollMessage(
                type,
                toAddr,
                question.getQuestion(),
                poll.getQuestionareId(),
                (int) SPARQApplication.getOwnAddress(),
                question.getQuestionId(),
                question.getFormat(),
                question.getOptions(),
                0,
                false,
                true,
                question.isMainQuestion()
        );
    }

}
