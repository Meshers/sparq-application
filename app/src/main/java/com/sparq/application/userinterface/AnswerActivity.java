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
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.model.AnswerItem;

import java.nio.charset.Charset;

public class AnswerActivity extends AppCompatActivity {

    private final static Charset CHARSET = Charset.forName("UTF-8");

    private TextView answerText;
    private TextView usernameText;
    private ImageView userImage;
    private ImageView like, share, unlike;

    private int answerId;
    private int answerCreatorId;
    private int threadId;
    private int threadCreatorId;
    private AnswerItem answer;

    private ApplicationLayerManager mApplicationLayerManager;

    public static final String THREAD_ID ="thread_id";
    public static final String CREATOR_ID ="creator_id";
    public static final String ANSWER_ID ="answer_id";
    public static final String ANSWER_CREATOR_ID ="answer_creator_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            threadId = extras.getInt(THREAD_ID);
            threadCreatorId = extras.getInt(CREATOR_ID);
            answerId = extras.getInt(ANSWER_ID);
            answerCreatorId = extras.getInt(ANSWER_CREATOR_ID);
        }

        mApplicationLayerManager = SPARQApplication.getApplicationLayerManager();

        answer = SPARQApplication.getConversationThread(
              threadId, threadCreatorId
        ).getAnswer(answerId, answerCreatorId);

        answerText = (TextView) findViewById(R.id.answer_text);
        answerText.setText(answer.getAnswer());
        usernameText = (TextView) findViewById(R.id.answer_username);
        usernameText.setText("Anonymous");
        userImage = (ImageView) findViewById(R.id.user_image);
        like = (ImageView) findViewById(R.id.like);
        share = (ImageView) findViewById(R.id.share);
        unlike = (ImageView) findViewById(R.id.unlike);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnswerActivity.this, "Your Vote has been recorded.", Toast.LENGTH_SHORT).show();

                mApplicationLayerManager.sendData(
                        ApplicationLayerPdu.TYPE.ANSWER_VOTE,
                        new String(
                                new byte[]{AlVote.getVoteEncoded(AlVote.VOTE_TYPE.UPVOTE)},
                                CHARSET
                        ),
                        SPARQApplication.getBdcastAddress(),
                        (byte) threadId,
                        (byte) threadCreatorId, (byte) answerId, (byte) answerCreatorId);

                answer.addUpVote();
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

                mApplicationLayerManager.sendData(
                        ApplicationLayerPdu.TYPE.ANSWER_VOTE,
                        new String(
                                new byte[]{AlVote.getVoteEncoded(AlVote.VOTE_TYPE.DOWNVOTE)},
                                CHARSET
                        ),
                        SPARQApplication.getBdcastAddress(),
                        (byte) threadId,
                        (byte) threadCreatorId, (byte) answerId, (byte) answerCreatorId);

                answer.addDownVote();
            }
        });


    }

}
