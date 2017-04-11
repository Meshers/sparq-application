package com.sparq.application.userinterface;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.sparq.application.userinterface.adapter.QuestionAdapter;
import com.sparq.application.userinterface.model.PollItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.Questionare;
import com.sparq.application.userinterface.model.QuizItem;
import com.sparq.application.userinterface.model.UserItem;
import com.sparq.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.sparq.application.userinterface.model.QuizItem.QUIZ_STATE.INACTIVE;

public class NewQuestionareActicity extends AppCompatActivity {

    private TextView questionareNameText;
//    private EditText descriptionText;
    private EditText durationText;
    private FloatingActionButton addQuestionare;
    private FloatingActionButton newQuestion;
    private RecyclerView questionsRecyclerView;

    private QuestionAdapter mQuestionAdapter;

    private Questionare.QUESTIONARE_TYPE type;
    private QuestionItem.FORMAT questionareFormat;
    Questionare questionare;
    private HashMap<Integer, QuestionItem> questionsArray = new HashMap<>(0);

    public static final String QUESTIONARE_TYPE = "questionare_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_questionare_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            type = (Questionare.QUESTIONARE_TYPE) bundle.getSerializable(QUESTIONARE_TYPE);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        switch(type){
            case QUIZ:
                questionare = new QuizItem(
                        SPARQApplication.getQuizzes().size()+1,
                        SPARQApplication.getSessionId(),
                        null, null,
                        new Date(),
                        Constants.QUIZ_DURATION,
                        INACTIVE,
                        Constants.MIN_QUESTION_MARKS,
                        new UserItem(SPARQApplication.getOwnAddress())
                );

                questionare.setName("Quiz " + questionare.getQuestionareId());
                break;
            case POLL:
                questionare = new PollItem(
                        SPARQApplication.getPolls().size()+1,
                        SPARQApplication.getSessionId(),
                        null, null,
                        new Date(),
                        PollItem.POLL_STATE.STOP,
                        new UserItem(SPARQApplication.getOwnAddress())
                );

                questionare.setName("Poll " + questionare.getQuestionareId());
                break;
        }



        initializeViews();

    }

    public void initializeViews(){

        switch(type){
            case QUIZ:
                setTitle("Create New Quiz");
                break;
            case POLL:
                setTitle("Create New Poll");
                break;
        }

        newQuestion = (FloatingActionButton) findViewById(R.id.fab_new_questonare);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(type){
                    case QUIZ:
                        openNewQuizQuestionDialog();
                        break;
                    case POLL:
                        openNewPollQuestionDialog();
                        break;
                }

            }
        });

        durationText = (EditText) findViewById(R.id.duration_text);
        questionsRecyclerView = (RecyclerView) findViewById(R.id.questionare_recycler_view);
        addQuestionare = (FloatingActionButton) findViewById(R.id.add_questionare);
        addQuestionare.setEnabled(false);

        addQuestionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add all the questions to the questionare object
                questionare.setQuestions(questionsArray);

                //Log.i("onClick: ", questionare.getName());
                //Setting first question as the main question for naming purpose
//                if(questionare.getName() == null){
//                    QuestionItem firstQuestion = questionsArray.get(new Integer(1));
//                    firstQuestion.setMainQuestion(true);
//                    questionare.setName(firstQuestion.getQuestion());
//                    Log.i("onClick: ", questionare.getName());
//                }
//                else{
//                    Log.i("onClick: ", questionare.getName());
//                }


                switch(type){
                    case QUIZ:
                        sendQuizMessage(
                                ApplicationLayerPdu.TYPE.QUIZ_QUESTION,
                                SPARQApplication.getBdcastAddress(),
                                (QuizItem) questionare
                        );
                        break;
                    case POLL:
                        sendPollMessage(
                                ApplicationLayerPdu.TYPE.POLL_QUESTION,
                                SPARQApplication.getBdcastAddress(),
                                (PollItem) questionare
                        );
                        break;
                }

                finish();
            }
        });

        mQuestionAdapter = new QuestionAdapter(NewQuestionareActicity.this,questionsArray);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewQuestionareActicity.this);
        questionsRecyclerView.setLayoutManager(mLayoutManager);
        questionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        questionsRecyclerView.setAdapter(mQuestionAdapter);

        mQuestionAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(mQuestionAdapter.getItemCount() == Constants.MAX_NUMBER_OF_QUESIONS){
                    newQuestion.setEnabled(false);
                }
                else{
                    newQuestion.setEnabled(true);
                }
            }
        });
    }

    public void openNewQuizQuestionDialog(){

        final String[] SPINNERLIST = {
                "Single Choice MCQ",
                "Multiple Choice MCQ",
        };

        final ArrayList<String> options = new ArrayList<>();

        final QuestionItem.FORMAT format[] = new QuestionItem.FORMAT[1];
        format[0] = QuestionItem.getFormatFromByte((byte) 1);

        final OptionsAdapter mAdapter = new OptionsAdapter(options);

        final MaterialDialog dialog = new MaterialDialog.Builder(NewQuestionareActicity.this)
                .title("Add a New Question")
                .customView(R.layout.dialog_new_question, true)
                .positiveText("ADD")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if((format[0] == QuestionItem.FORMAT.MCQ_SINGLE || format[0] == QuestionItem.FORMAT.MCQ_MULTIPLE)
                                && mAdapter.getItemCount() < 2){

                            Toast.makeText(NewQuestionareActicity.this, getResources().getString(R.string.more_options),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            QuestionItem newQuestion = new QuestionItem(
                                    questionsArray.size()+1,
                                    questionare.getQuestionareId(),
                                    "Question " + (questionsArray.size()+1),
                                    format[0],
                                    Constants.MIN_QUESTION_MARKS,
                                    mAdapter.getOptions(),
                                    Constants.INITIAL_VOTE_COUNT
                            );
                            questionsArray.put(questionsArray.size()+1, newQuestion);

                            //notify dataset changed
                            mQuestionAdapter.notifyDataSetChanged();

                            dialog.dismiss();
                            addQuestionare.setEnabled(true);

                        }

                        questionareFormat = format[0];
                        dialog.dismiss();
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

        EditText questionName = (EditText) view.findViewById(R.id.question_text);
        questionName.setVisibility(View.GONE);

        SwitchCompat mainQuestion = (SwitchCompat) view.findViewById(R.id.switchButton);
        mainQuestion.setVisibility(View.GONE);

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

                }
            }

        });


        final EditText option = (EditText) view.findViewById(R.id.option_text);
        final ImageView addOption = (ImageView) view.findViewById(R.id.add_option);

        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(option.getText().toString().compareTo("") != 0 && options.contains(option.getText().toString()) == false){
                    options.add(option.getText().toString());
                    mAdapter.notifyDataSetChanged();
                    option.setText("");
                }

            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(mAdapter.getItemCount() >= 2){
                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
                else{
                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }

                if(mAdapter.getItemCount() == Constants.MAX_NUMBER_OF_OPTIONS){
                    addOption.setEnabled(false);
                }
                else{
                    addOption.setEnabled(true);
                }

            }
        });

        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if((format[0] == QuestionItem.FORMAT.MCQ_SINGLE || format[0] == QuestionItem.FORMAT.MCQ_MULTIPLE)
                        && mAdapter.getItemCount() < 2){
                    ((MaterialDialog)dialog).getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
                else{
                    ((MaterialDialog)dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            }
        });


    }

    public void openNewPollQuestionDialog(){

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

        final MaterialDialog dialog = new MaterialDialog.Builder(NewQuestionareActicity.this)
                .title("Add a New Question")
                .customView(R.layout.dialog_new_question, true)
                .positiveText("ADD")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                        View view = dialog.getCustomView();
                        final EditText questionName = (EditText) view.findViewById(R.id.question_text);
                        SwitchCompat mainQuestion = (SwitchCompat) view.findViewById(R.id.switchButton);

                        if((format[0] == QuestionItem.FORMAT.MCQ_SINGLE || format[0] == QuestionItem.FORMAT.MCQ_MULTIPLE)
                                && mAdapter.getItemCount() < 2){

                            Toast.makeText(NewQuestionareActicity.this, getResources().getString(R.string.more_options),
                                    Toast.LENGTH_SHORT).show();

                        }else if(questionName.getText().toString().length() == 0) {

                            Toast.makeText(NewQuestionareActicity.this, getResources().getString(R.string.empty_question_msg),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{

                            QuestionItem newQuestion = new QuestionItem(
                                    questionsArray.size()+1,
                                    questionare.getQuestionareId(),
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
                                questionare.setName(questionName.getText().toString());
                            }

                            dialog.dismiss();
                            //Enabling the add button when atleast one question is present
                            addQuestionare.setEnabled(true);
                        }

                        questionareFormat = format[0];
                        dialog.dismiss();
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
                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);

                } else if(item.equalsIgnoreCase(SPINNERLIST[3])){
                    format[0] = QuestionItem.getFormatFromByte((byte) 4);
                    hideLayout.setVisibility(View.GONE);
                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            }

        });


        final EditText option = (EditText) view.findViewById(R.id.option_text);
        final ImageView addOption = (ImageView) view.findViewById(R.id.add_option);

        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(option.getText().toString().compareTo("") != 0 && options.contains(option.getText().toString()) == false){
                    options.add(option.getText().toString());
                    mAdapter.notifyDataSetChanged();
                    option.setText("");
                }

            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(mAdapter.getItemCount() >= 2){
                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
                else{
                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }

                if(mAdapter.getItemCount() == Constants.MAX_NUMBER_OF_OPTIONS){
                    addOption.setEnabled(false);
                }
                else{
                    addOption.setEnabled(true);
                }

            }
        });

        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if((format[0] == QuestionItem.FORMAT.MCQ_SINGLE || format[0] == QuestionItem.FORMAT.MCQ_MULTIPLE)
                        && options.size() < 2){
                    ((MaterialDialog)dialog).getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
                else{
                    ((MaterialDialog)dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            }
        });

        dialog.setCanceledOnTouchOutside(false);
    }

    public void sendQuizMessage(final ApplicationLayerPdu.TYPE type, final byte toAddr, final QuizItem quiz){

        Log.i("HERE", "send quiz Message");
        final ArrayList<QuestionItem> questions = new ArrayList<>(quiz.getQuestions().values());

        HashMap<Integer, ArrayList<String>> bundledOptions = new HashMap<>(0);
        HashMap<Integer, String> bundledMessage = new HashMap<>(0);

        for(int i = 0; i < questions.size(); i++){
            QuestionItem question = questions.get(i);
            bundledOptions.put(question.getQuestionId(), question.getOptions());
            bundledMessage.put(question.getQuestionId(), String.valueOf(question.getOptions().size()));
        }

        SPARQApplication.sendQuizMessage(
                type,
                toAddr,
                bundledMessage,
                quiz.getQuestionareId(),
                (int) SPARQApplication.getOwnAddress(),
                questionareFormat,
                questions.size(),
                bundledOptions,
                0
        );
        Log.i("HERE", "exiting send quiz Message");
    }

//    public void sendPollMessage(final ApplicationLayerPdu.TYPE type, final byte toAddr, final PollItem poll){
//
//        final ArrayList<QuestionItem> questions = new ArrayList<>(poll.getQuestions().values());
//        int i = 0;
//        for(; i < questions.size() - 1; i++) {
//
//            QuestionItem question = questions.get(i);
//            SPARQApplication.sendPollMessage(
//                    type,
//                    toAddr,
//                    question.getQuestion(),
//                    poll.getQuestionareId(),
//                    (int) SPARQApplication.getOwnAddress(),
//                    question.getQuestionId(),
//                    question.getFormat(),
//                    question.getOptions(),
//                    0,
//                    true,
//                    false,
//                    question.isMainQuestion()
//            );
//
//        }
//
//        QuestionItem question = questions.get(i);
//
//        SPARQApplication.sendPollMessage(
//                type,
//                toAddr,
//                question.getQuestion(),
//                poll.getQuestionareId(),
//                (int) SPARQApplication.getOwnAddress(),
//                question.getQuestionId(),
//                question.getFormat(),
//                question.getOptions(),
//                0,
//                false,
//                true,
//                question.isMainQuestion()
//        );
//    }

    public void sendPollMessage(final ApplicationLayerPdu.TYPE type, final byte toAddr, final PollItem poll){

        Log.i("HERE", "send poll Message");
        final ArrayList<QuestionItem> questions = new ArrayList<>(poll.getQuestions().values());

        HashMap<Integer, ArrayList<String>> bundledOptions = new HashMap<>(0);
        HashMap<Integer, String> bundledMessage = new HashMap<>(0);

        for(int i = 0; i < questions.size(); i++){
            QuestionItem question = questions.get(i);
            bundledOptions.put(question.getQuestionId(), question.getOptions());
            bundledMessage.put(question.getQuestionId(), String.valueOf(question.getOptions().size()));
        }

        SPARQApplication.sendPollMessage(
                type,
                toAddr,
                bundledMessage,
                poll.getQuestionareId(),
                (int) SPARQApplication.getOwnAddress(),
                questionareFormat,
                questions.size(),
                bundledOptions,
                0
        );
    }


}
