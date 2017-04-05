package com.sparq.application.userinterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sparq.R;

public class NewQuestionareActicity extends AppCompatActivity {

    private EditText questionareNameText;
    private EditText descriptionText;
    private EditText durationText;
    private Button addQuestionare;
    private RecyclerView questions;

    private String questionareName;
    private String description;
    private int duration;

    public static final String QUESTIONARE_TYPE = "questionare_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_questionare_acticity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();

    }

    public void initializeViews(){

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_questonare);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewQuestionDalog();
            }
        });

        questionareNameText = (EditText) findViewById(R.id.questionareName_text);
        descriptionText = (EditText) findViewById(R.id.description_text);
        durationText = (EditText) findViewById(R.id.duration_text);
        questions = (RecyclerView) findViewById(R.id.questionare_recycler_view);
        addQuestionare = (Button) findViewById(R.id.add_questionare);

        addQuestionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questionareName = questionareNameText.getText().toString();
                description = descriptionText.getText().toString();
                duration = Integer.parseInt(durationText.getText().toString());
            }
        });
    }

    public void openNewQuestionDalog(){
        MaterialDialog dialog = new MaterialDialog.Builder(NewQuestionareActicity.this)
                .title("Add a New Question")
                .customView(R.layout.dialog_new_question, true)
                .positiveText("ADD")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        // add to arraylist

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();

        View view = dialog.getCustomView();


        dialog.show();
    }

}
