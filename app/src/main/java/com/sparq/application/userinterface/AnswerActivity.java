package com.sparq.application.userinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.QuestionItem;

import java.nio.charset.Charset;

public class AnswerActivity extends AppCompatActivity {

    private final static Charset CHARSET = Charset.forName("UTF-8");

    public final static String TAG = "AnswerActivity";

    private TextView questionText;
    private TextView answerText;
    private TextView usernameText;
    private TextView answerVotes;
    private ImageView userImage;
    private ImageView like, share, unlike;

    private int answerId;
    private int answerCreatorId;
    private int threadId;
    private int threadCreatorId;

    private QuestionItem question;
    private AnswerItem answer;

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

            Log.i(THREAD_ID, String.valueOf(threadId));
            Log.i(CREATOR_ID, String.valueOf(threadCreatorId));
            Log.i(ANSWER_ID, String.valueOf(answerId));
            Log.i(ANSWER_CREATOR_ID, String.valueOf(answerCreatorId));
        }

        question = SPARQApplication.getConversationThread(
                threadId, threadCreatorId
        ).getQuestionItem();

        answer = SPARQApplication.getConversationThread(
                threadId, threadCreatorId
        ).getAnswer(answerId, answerCreatorId);

        initializeViews();

    }

    public void initializeViews(){

        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText(question.getQuestion());

        answerText = (TextView) findViewById(R.id.answer_text);
        answerText.setText(answer.getAnswer());

        usernameText = (TextView) findViewById(R.id.answer_username);

        // FIXME: 2/4/17
        usernameText.setText("Anonymous");

        answerVotes = (TextView) findViewById(R.id.answer_votes);
        answerVotes.setText(String.valueOf(answer.getVotes()));

        userImage = (ImageView) findViewById(R.id.user_image);

        like = (ImageView) findViewById(R.id.like);
        share = (ImageView) findViewById(R.id.share);
        unlike = (ImageView) findViewById(R.id.unlike);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SPARQApplication.sendMessage(
                        ApplicationLayerPdu.TYPE.ANSWER_VOTE,
                        SPARQApplication.getBdcastAddress(),
                        null,
                        threadCreatorId,
                        threadId,
                        answerCreatorId,
                        answerId,
                        AlVote.VOTE_TYPE.UPVOTE);

                Toast.makeText(AnswerActivity.this, getResources().getString(R.string.vote_recorded), Toast.LENGTH_SHORT).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnswerActivity.this, getResources().getString(R.string.func_added_soon), Toast.LENGTH_SHORT).show();
            }
        });

        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SPARQApplication.sendMessage(
                        ApplicationLayerPdu.TYPE.ANSWER_VOTE,
                        SPARQApplication.getBdcastAddress(),
                        null,
                        threadCreatorId,
                        threadId,
                        answerCreatorId,
                        answerId,
                        AlVote.VOTE_TYPE.DOWNVOTE);

                Toast.makeText(AnswerActivity.this, getResources().getString(R.string.vote_recorded), Toast.LENGTH_SHORT).show();
            }
        });

        if(answer.hasVoted()){
            // deactivate the vote buttons
            deactivateVote();
        }
    }

    public void deactivateVote(){

        like.setBackgroundResource(R.drawable.ic_like_disabled);
        like.setEnabled(false);

        unlike.setBackgroundResource(R.drawable.ic_unlike_disabled);
        unlike.setEnabled(false);
    }

    @Override
    public void onResume(){
        super.onResume();

        NotifyUIHandler uiHandler = new NotifyUIHandler() {
            @Override
            public void handleConversationThreadQuestions() {
                // do nothing
            }

            @Override
            public void handleConversationThreadAnswers(){
                // do nothing
            }

            @Override
            public void handleConversationThreadAnswerVotes(){
                answerVotes.setText(String.valueOf(answer.getVotes()));

                // deactivate the vote buttons
                deactivateVote();
            }
        };

        SPARQApplication.setUINotifier(uiHandler);
    }

}
