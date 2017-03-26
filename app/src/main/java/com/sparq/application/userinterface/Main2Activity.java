package com.sparq.application.userinterface;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

import com.sparq.application.MyApplication;
import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlAnswer;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.almessage.AlQuestion;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import com.sparq.R;

import java.nio.charset.Charset;


public class Main2Activity extends AppCompatActivity {

    private final static Charset CHARSET = Charset.forName("UTF-8");

    private Button mQuestionBtn, mAnsBtn, mQuestionVoteBtn, mAnsVoteBtn;
    private EditText mToId, mFromId;
    private EditText mBtMessage;

    private byte mOwnAddr = (byte) 1;
    private byte mEventCode = (byte) 1;

    private MyBluetoothAdapter mBluetoothAdapter;
    ApplicationLayerManager mApplicationLayerManager;

    public final static String EXTRA_EVENT_CODE = "EVENT_CODE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(test.com.blootoothtester.R.id.toolbar);
        setSupportActionBar(toolbar);

        mOwnAddr = MyApplication.getOwnAddress();
        mEventCode = getIntent().getByteExtra(EXTRA_EVENT_CODE, (byte) -1);

        initialize();

        mBluetoothAdapter = new MyBluetoothAdapter(Main2Activity.this);


        ApplicationPacketDiscoveryHandler mHandler = new ApplicationPacketDiscoveryHandler() {
            @Override
            public void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage alMessage) {

                switch(type){
                    case QUESTION:

                        AlQuestion alQuestion = (AlQuestion) alMessage;
                        Toast.makeText(Main2Activity.this, "RECEIVED MESSAGE: "
                                + alQuestion.getCreatorId() + ":" +alQuestion.getQuestionId()
                                ,Toast.LENGTH_SHORT).show();

                        break;
                    case ANSWER:

                        AlAnswer alAnswer = (AlAnswer) alMessage;
                        Toast.makeText(Main2Activity.this, "RECEIVED MESSAGE: "
                                + alAnswer.getCreatorId() + ":"
                                + alAnswer.getQuestionId()+ ":"
                                + alAnswer.getAnswerCreatorId()+ ":"
                                + alAnswer.getAnswerId()+ ":"
                                ,Toast.LENGTH_SHORT).show();

                        break;
                    case QUESTION_VOTE:

                        AlVote alQuestionVote = (AlVote) alMessage;
                        Toast.makeText(Main2Activity.this, "RECEIVED MESSAGE: "
                                + alQuestionVote.getCreatorId() + ":"
                                + alQuestionVote.getQuestionId() +":"
                                + alQuestionVote.getVoteValue()
                                ,Toast.LENGTH_SHORT).show();

                        break;
                    case ANSWER_VOTE:

                        AlVote alAnswerVote = (AlVote) alMessage;
                        Toast.makeText(Main2Activity.this, "RECEIVED MESSAGE: "
                                        + alAnswerVote.getCreatorId() + ":"
                                        + alAnswerVote.getQuestionId()+ ":"
                                        + alAnswerVote.getAnswerCreatorId()+ ":"
                                        + alAnswerVote.getAnswerId()+ ":"
                                        + alAnswerVote.getVoteValue()
                                ,Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        throw new IllegalArgumentException("Illegal message type.");
                }
            }
        };

        mApplicationLayerManager = new ApplicationLayerManager(mOwnAddr, mBluetoothAdapter, mHandler, MyApplication.getSessionId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // this takes care of letting the user add the WRITE_SETTINGS permission
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }


        if (!mBluetoothAdapter.isSupported()) {
            mQuestionBtn.setEnabled(false);

            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {

            mQuestionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMessage(mBtMessage.getText().toString(), ApplicationLayerPdu.TYPE.QUESTION);
                }
            });

            mAnsBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMessage(mBtMessage.getText().toString(), ApplicationLayerPdu.TYPE.ANSWER);
                }
            });

            mQuestionVoteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMessage(mBtMessage.getText().toString(), ApplicationLayerPdu.TYPE.QUESTION_VOTE);
                }
            });

            mAnsVoteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMessage(mBtMessage.getText().toString(), ApplicationLayerPdu.TYPE.ANSWER_VOTE);
                }
            });
        }



//        mApplicationLayerManager = MyApplication.getApplicationLayerManager();
    }



    public void initialize() {
        mFromId = (EditText) findViewById(R.id.et_from_id);
        mToId = (EditText) findViewById(R.id.et_to_id);
        mBtMessage = (EditText) findViewById(R.id.bluetooth_message);
        mQuestionBtn = (Button) findViewById(R.id.send_question);
        mAnsBtn = (Button) findViewById(R.id.send_ans);
        mQuestionVoteBtn = (Button) findViewById(R.id.send_question_vote);
        mAnsVoteBtn = (Button) findViewById(R.id.send_ans_vote);

    }

    public void sendMessage(String msg, ApplicationLayerPdu.TYPE type){

        switch(type){
            case QUESTION:
                mApplicationLayerManager.sendData(
                        ApplicationLayerPdu.TYPE.QUESTION,
                        msg,
                        Byte.parseByte(mToId.getText().toString()),
                        (byte) 1,
                        (byte) 1, (byte) 1, (byte) 1);
                break;
            case ANSWER:
                mApplicationLayerManager.sendData(
                        ApplicationLayerPdu.TYPE.ANSWER,
                        msg,
                        Byte.parseByte(mToId.getText().toString()),
                        (byte) 1,
                        (byte) 1, (byte) 1, (byte) 1);
                break;
            case QUESTION_VOTE:
                mApplicationLayerManager.sendData(
                        ApplicationLayerPdu.TYPE.QUESTION_VOTE,
                        new String(
                                new byte[]{AlVote.getVoteEncoded(AlVote.VOTE_TYPE.UPVOTE)},
                                CHARSET
                        ),
                        Byte.parseByte(mToId.getText().toString()),
                        (byte) 1,
                        (byte) 1, (byte) 1, (byte) 1);
                break;
            case ANSWER_VOTE:
                mApplicationLayerManager.sendData(
                        ApplicationLayerPdu.TYPE.ANSWER_VOTE,
                        new String(
                                new byte[]{AlVote.getVoteEncoded(AlVote.VOTE_TYPE.UPVOTE)},
                                CHARSET
                        ),
                        Byte.parseByte(mToId.getText().toString()),
                        (byte) 1,
                        (byte) 1, (byte) 1, (byte) 1);
                break;
        }
        Toast.makeText(Main2Activity.this, "Message sent!", Toast.LENGTH_SHORT).show();


    }


}

