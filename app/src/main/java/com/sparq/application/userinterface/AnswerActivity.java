package com.sparq.application.userinterface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.R;
import com.sparq.application.userinterface.model.AnswerItem;

public class AnswerActivity extends AppCompatActivity {

    private TextView answerText;
    private TextView usernameText;
    private ImageView userImage;
    private ImageView like, share, unlike;

    private AnswerItem answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        answer = (AnswerItem) getIntent().getSerializableExtra("Answer");

        answerText = (TextView) findViewById(R.id.answer_text);
        answerText.setText(answer.getAnswer());
        usernameText = (TextView) findViewById(R.id.answer_username);
        usernameText.setText("Jane Doe");
        userImage = (ImageView) findViewById(R.id.user_image);
        like = (ImageView) findViewById(R.id.like);
        share = (ImageView) findViewById(R.id.share);
        unlike = (ImageView) findViewById(R.id.unlike);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnswerActivity.this, "Your Vote has been recorded.", Toast.LENGTH_SHORT).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnswerActivity.this, "Your Vote has been recorded.", Toast.LENGTH_SHORT).show();
            }
        });

        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnswerActivity.this, "Your Vote has been recorded.", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
