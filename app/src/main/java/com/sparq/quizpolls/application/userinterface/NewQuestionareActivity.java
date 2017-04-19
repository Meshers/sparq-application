package com.sparq.quizpolls.application.userinterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.SPARQApplication;
import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.quizpolls.application.userinterface.adapter.OptionsAdapter;
import com.sparq.quizpolls.application.userinterface.adapter.QuestionAdapter;
import com.sparq.quizpolls.application.userinterface.model.PollItem;
import com.sparq.quizpolls.application.userinterface.model.QuestionItem;
import com.sparq.quizpolls.application.userinterface.model.Questionare;
import com.sparq.quizpolls.application.userinterface.model.QuizItem;
import com.sparq.quizpolls.application.userinterface.model.UserItem;
import com.sparq.quizpolls.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.sparq.quizpolls.application.userinterface.model.Questionare.QUESTIONARE_TYPE.POLL;
import static com.sparq.quizpolls.application.userinterface.model.Questionare.QUESTIONARE_TYPE.QUIZ;
import static com.sparq.quizpolls.application.userinterface.model.QuizItem.QUIZ_STATE.INACTIVE;

public class NewQuestionareActivity extends AppCompatActivity {

    final String[] SPINNERLIST = {
            "Single Choice MCQ",
            "Multiple Choice MCQ",
    };

    RecyclerView optionsListView;
    OptionsAdapter mOptionsAdapter;
    ArrayList<String> options;

    private TextView questionareNameText;
    private EditText durationText;
    private FloatingActionButton addQuestionare;
    private FloatingActionButton newQuestion;
    private RecyclerView questionsRecyclerView;

    private QuestionAdapter mQuestionAdapter;

    private Questionare.QUESTIONARE_TYPE type;
    private QuestionItem.FORMAT questionareFormat = QuestionItem.FORMAT.MCQ_SINGLE;
    Questionare questionare;
    private ArrayList<QuestionItem> questionsArray = new ArrayList<>(0);

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

        // set up the format spinner
        final MaterialSpinner formatSpinner = (MaterialSpinner) findViewById(R.id.format_spinner);
        formatSpinner.setItems(SPINNERLIST);
        formatSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item.equalsIgnoreCase(SPINNERLIST[0])){
                    questionareFormat = QuestionItem.getFormatFromByte((byte) 1);


                } else if(item.equalsIgnoreCase(SPINNERLIST[1])){
                    questionareFormat = QuestionItem.getFormatFromByte((byte) 2);

                }

                initializeQuestionAdapter();
            }

        });

        newQuestion = (FloatingActionButton) findViewById(R.id.fab_new_questonare);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openNewQuestionDialog();

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
                questionare.setQuestions(mQuestionAdapter.getQuestionHash());

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

        initializeQuestionAdapter();
    }

    public void initializeQuestionAdapter(){

        questionsArray.clear();

        mQuestionAdapter = new QuestionAdapter(NewQuestionareActivity.this,questionsArray);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewQuestionareActivity.this);
        questionsRecyclerView.setLayoutManager(mLayoutManager);
        questionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        questionsRecyclerView.setAdapter(mQuestionAdapter);

        mQuestionAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(mQuestionAdapter.getItemCount() == Constants.MAX_NUMBER_OF_QUESTIONS ){
                    newQuestion.setEnabled(false);
                }
                else{
                    newQuestion.setEnabled(true);
                }

                if(mQuestionAdapter.getItemCount() == 0){
                    addQuestionare.setEnabled(false);
                }
                else{
                    addQuestionare.setEnabled(true);
                }

            }
        });
    }


    public void openNewQuestionDialog(){

        final String[] SPINNEROPTIONLIST = {
                "Options", "2", "3", "4", "5"
        };

        options = new ArrayList<>();
        mOptionsAdapter = new OptionsAdapter(type, questionareFormat , options);

        final MaterialDialog dialog = new MaterialDialog.Builder(NewQuestionareActivity.this)
                .title("Mark the right answers")
                .customView(R.layout.dialog_new_question_dup, true)
                .positiveText("ADD")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        QuestionItem newQuestion = new QuestionItem(
                                0,
                                questionare.getQuestionareId(),
                                "New Question",
                                questionareFormat,
                                Constants.MIN_QUESTION_MARKS,
                                mOptionsAdapter.getOptions(),
                                Constants.INITIAL_VOTE_COUNT,
                                mOptionsAdapter.getCorrectOptions()
                        );
                        questionsArray.add(newQuestion);

                        //notify dataset changed
                        mQuestionAdapter.notifyDataSetChanged();
                        addQuestionare.setEnabled(true);

                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();

        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        View view = dialog.getCustomView();

        optionsListView = (RecyclerView) view.findViewById(R.id.options_recycler_view);
//        initializeOptionsAdapter();

        // set up the options spinner
        final MaterialSpinner optionsSpinner = (MaterialSpinner) view.findViewById(R.id.option_spinner);
        optionsSpinner.setItems(SPINNEROPTIONLIST);
        optionsSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                initializeOptionsAdapter(dialog);
                if(item.equalsIgnoreCase("Options")){
                    mOptionsAdapter.notifyDataSetChanged();
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

                    return;
                }

                int choice = Integer.parseInt(item);
                for(int i = 1; i <= choice; i++){
                    options.add(String.valueOf(i));
                }

                mOptionsAdapter.notifyDataSetChanged();
            }

        });

        dialog.show();

        mOptionsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.i("onChanged: ", String.valueOf(mOptionsAdapter.getItemCount()) + ";" + String.valueOf(mOptionsAdapter.getChosenAnswerCount()));
                if(mOptionsAdapter.getItemCount() >= 2){

                    if( type == QUIZ && mOptionsAdapter.getChosenAnswerCount() != 0){
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    else if(type == POLL){
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    else{
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                }
                else{
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
            }
        });

    }

    public  void  initializeOptionsAdapter(final MaterialDialog dialog){
        options.clear();
        mOptionsAdapter = new OptionsAdapter(type, questionareFormat , options);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewQuestionareActivity.this);
        optionsListView.setLayoutManager(mLayoutManager);
        optionsListView.setItemAnimator(new DefaultItemAnimator());
        optionsListView.setAdapter(mOptionsAdapter);

        mOptionsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.i("onChanged: ", String.valueOf(mOptionsAdapter.getItemCount()) + ";" + String.valueOf(mOptionsAdapter.getChosenAnswerCount()));
                if(mOptionsAdapter.getItemCount() >= 2){

                    if( type == QUIZ && mOptionsAdapter.getChosenAnswerCount() != 0){
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    else if(type == POLL){
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    else{
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                }
                else{
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
            }
        });
    }


//    public void openNewPollQuestionDialog(){
//
//        final String[] SPINNERLIST = {
//                "Single Choice MCQ",
//                "Multiple Choice MCQ",
//                "One Word Answers",
//                "Short Answers"
//        };
//
//        final ArrayList<String> options = new ArrayList<>();
//
//        final QuestionItem.FORMAT format[] = new QuestionItem.FORMAT[1];
//        format[0] = QuestionItem.getFormatFromByte((byte) 1);
//
//        final OptionsAdapter mAdapter = new OptionsAdapter(options);
//
//        final MaterialDialog dialog = new MaterialDialog.Builder(NewQuestionareActivity.this)
//                .title("Add a New Question")
//                .customView(R.layout.dialog_new_question, true)
//                .positiveText("ADD")
//                .negativeText("CANCEL")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
//
//                        View view = dialog.getCustomView();
//                        final EditText questionName = (EditText) view.findViewById(R.id.question_text);
//                        SwitchCompat mainQuestion = (SwitchCompat) view.findViewById(R.id.switchButton);
//
//                        if((format[0] == QuestionItem.FORMAT.MCQ_SINGLE || format[0] == QuestionItem.FORMAT.MCQ_MULTIPLE)
//                                && mAdapter.getItemCount() < 2){
//
//                            Toast.makeText(NewQuestionareActivity.this, getResources().getString(R.string.more_options),
//                                    Toast.LENGTH_SHORT).show();
//
//                        }else if(questionName.getText().toString().length() == 0) {
//
//                            Toast.makeText(NewQuestionareActivity.this, getResources().getString(R.string.empty_question_msg),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//
//                            QuestionItem newQuestion = new QuestionItem(
//                                    questionsArray.size()+1,
//                                    questionare.getQuestionareId(),
//                                    questionName.getText().toString(),
//                                    format[0],
//                                    Constants.MIN_QUESTION_MARKS,
//                                    mAdapter.getOptions(),
//                                    Constants.INITIAL_VOTE_COUNT
//                            );
//                            questionsArray.put(questionsArray.size()+1, newQuestion);
//
//                            //notify dataset changed
//                            mQuestionAdapter.notifyDataSetChanged();
//
//                            if(mainQuestion.isChecked()){
//                                newQuestion.setMainQuestion(true);
//                                questionare.setName(questionName.getText().toString());
//                            }
//
//                            dialog.dismiss();
//                            //Enabling the add button when atleast one question is present
//                            addQuestionare.setEnabled(true);
//                        }
//
//                        questionareFormat = format[0];
//                        dialog.dismiss();
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                }).build();
//
//        View view = dialog.getCustomView();
//
//        final LinearLayout hideLayout = (LinearLayout) view.findViewById(R.id.hideable_layout);
//        hideLayout.setVisibility(View.GONE);
//
//        final RecyclerView optionsListView = (RecyclerView) view.findViewById(R.id.options_recycler_view);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewQuestionareActivity.this);
//        optionsListView.setLayoutManager(mLayoutManager);
//        optionsListView.setItemAnimator(new DefaultItemAnimator());
//        optionsListView.setAdapter(mAdapter);
//
//        final MaterialSpinner formatSpinner = (MaterialSpinner) view.findViewById(R.id.format_spinner);
//        formatSpinner.setItems(SPINNERLIST);
//
//        formatSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//
//                if(item.equalsIgnoreCase(SPINNERLIST[0])){
//                    format[0] = QuestionItem.getFormatFromByte((byte) 1);
//
//                } else if(item.equalsIgnoreCase(SPINNERLIST[1])){
//                    format[0] = QuestionItem.getFormatFromByte((byte) 2);
//                }
//                hideLayout.setVisibility(View.VISIBLE);
//            }
//
//        });
//
//
//        final EditText option = (EditText) view.findViewById(R.id.option_text);
//        final ImageView addOption = (ImageView) view.findViewById(R.id.add_option);
//
//        addOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(option.getText().toString().compareTo("") != 0 && options.contains(option.getText().toString()) == false){
//                    options.add(option.getText().toString());
//                    mAdapter.notifyDataSetChanged();
//                    option.setText("");
//                }
//
//            }
//        });
//
//        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                if(mAdapter.getItemCount() >= 2){
//                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                }
//                else{
//                    (dialog).getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                }
//
//                if(mAdapter.getItemCount() == Constants.MAX_NUMBER_OF_OPTIONS){
//                    addOption.setEnabled(false);
//                }
//                else{
//                    addOption.setEnabled(true);
//                }
//
//            }
//        });
//
//        dialog.show();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                if((format[0] == QuestionItem.FORMAT.MCQ_SINGLE || format[0] == QuestionItem.FORMAT.MCQ_MULTIPLE)
//                        && options.size() < 2){
//                    ((MaterialDialog)dialog).getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                }
//                else{
//                    ((MaterialDialog)dialog).getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                }
//            }
//        });
//
//        dialog.setCanceledOnTouchOutside(false);
//    }

    public void sendQuizMessage(final ApplicationLayerPdu.TYPE type, final byte toAddr, final QuizItem quiz){

        final ArrayList<QuestionItem> questions = new ArrayList<>(quiz.getQuestions().values());

        HashMap<Integer, ArrayList<String>> bundledOptions = new HashMap<>(0);
        HashMap<Integer, String> bundledMessage = new HashMap<>(0);
        HashMap<Integer, String> correctAnswers = new HashMap<>(0);
        HashMap<Integer, ArrayList<Integer>> correctOptions = new HashMap<>(0);

        for(int i = 0; i < questions.size(); i++){
            QuestionItem question = questions.get(i);
            bundledOptions.put(question.getQuestionId(), question.getOptions());
            bundledMessage.put(question.getQuestionId(), String.valueOf(question.getOptions().size()));
            correctAnswers.put(question.getQuestionId(), question.getCorrectAnswer());
            correctOptions.put(question.getQuestionId(), question.getCorrectOptions());
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
                0,
                correctAnswers,
                correctOptions
        );
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
