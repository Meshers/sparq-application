package com.sparq.application.userinterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.EventPagerAdapter;
import com.sparq.application.userinterface.model.Questionare;
import com.sparq.util.Constants;

import java.util.ArrayList;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

import static com.sparq.application.SPARQApplication.SPARQInstance;
import static com.sparq.application.userinterface.model.QuestionItem.FORMAT.MCQ_MULTIPLE;
import static com.sparq.application.userinterface.model.QuestionItem.FORMAT.MCQ_SINGLE;
import static com.sparq.application.userinterface.model.QuestionItem.FORMAT.ONE_WORD;
import static com.sparq.application.userinterface.model.QuestionItem.FORMAT.SHORT;

//import android.support.design.widget.FloatingActionButton;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    TabLayout tabLayout;
    public FloatingActionsMenu newEvent;
    FloatingActionButton newQuiz ,newPoll, newConvThread;

    private MyBluetoothAdapter mBluetoothAdapter;
    private ApplicationPacketDiscoveryHandler mHandler;
    private ApplicationLayerManager mManager;

    //broadcast receiver
    BroadcastReceiver timerReceiver;
    boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        int eventId = bundle.getInt("event_id");

        // add event name here
        setTitle("Event 0");

        initializeViews();

//        SPARQApplication.initializeObjects(EventActivity.this);

        initializeLowerLayer();

        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.addTab(tabLayout.newTab().setText("Quiz"));
        tabLayout.addTab(tabLayout.newTab().setText("Poll"));
        tabLayout.addTab(tabLayout.newTab().setText("Thread"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final EventPagerAdapter adapter = new EventPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        timerReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equalsIgnoreCase(Constants.UI_ENABLE_BROADCAST_INTENT)){
                    Log.i(TAG, "received");
                    SPARQApplication.setIsTimerElapsed(true);
                    newEvent.setEnabled(true);
                }
                else if(action.equalsIgnoreCase(Constants.UI_DISABLE_BROADCAST_INTENT)){
                    newEvent.setEnabled(false);
                }

            }
        };
    }

    public void initializeViews(){

        newEvent = (FloatingActionsMenu) findViewById(R.id.fab);

        newQuiz = (FloatingActionButton) findViewById(R.id.fab_new_quiz);
        newPoll = (FloatingActionButton) findViewById(R.id.fab_new_poll);
        newConvThread = (FloatingActionButton) findViewById(R.id.fab_new_thread);

        newPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(EventActivity.this, Main2Activity.class);
//                intent.putExtra(NewQuestionareActicity.QUESTIONARE_TYPE, Questionare.QUESTIONARE_TYPE.POLL);
//                startActivity(intent);

                ArrayList<String> options = new ArrayList<String>();
                options.add("oneeee");
                options.add("two");

                SPARQApplication.sendPollMessage(
                        ApplicationLayerPdu.TYPE.POLL_QUESTION,
                        (byte) 41,
                        "hello world",
                        1,
                        1,
                        1,
                        SHORT,
                        null,
                        0,
                        false,false, true
                );

                newEvent.collapse();
            }
        });

        newQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(EventActivity.this, Main2Activity.class);
//                intent.putExtra(NewQuestionareActicity.QUESTIONARE_TYPE, Questionare.QUESTIONARE_TYPE.POLL);
//                startActivity(intent);

                ArrayList<String> options = new ArrayList<String>();
                options.add("oneeee");
                options.add("two");

                SPARQApplication.sendPollMessage(
                        ApplicationLayerPdu.TYPE.POLL_ANSWER,
                        (byte) 41,
                        "hello world i am here",
                        1,
                        1,
                        1,
                        SHORT,
                        null,
                        1,
                        false,false, true
                );

                newEvent.collapse();
            }
        });

        newConvThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

                newEvent.collapse();
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    }

    public void initializeLowerLayer(){
        mBluetoothAdapter = new MyBluetoothAdapter(EventActivity.this);

        mHandler = new ApplicationPacketDiscoveryHandler() {
            @Override
            public void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage message) {
                SPARQApplication.handlePackets(type, message);
            }
        };

        mManager = new ApplicationLayerManager(SPARQApplication.getOwnAddress(), mBluetoothAdapter, mHandler,SPARQApplication.getSessionId());

        SPARQApplication.setApplicationLayerManager(mManager);
    }

    public void openDialog(){

        MaterialDialog dialog = new MaterialDialog.Builder(EventActivity.this)
                .title(getResources().getString(R.string.new_question))
                .customView(R.layout.dialog_new_question, true)
                .positiveText("POST")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        View v = dialog.getCustomView();
                        EditText questionText = (EditText) v.findViewById(R.id.question_text);

                        if(questionText != null
                            && questionText.getText().toString().isEmpty()){
                            Toast.makeText(EventActivity.this, getResources().getString(R.string.empty_question_msg),
                                    Toast.LENGTH_SHORT).show();

                            //TODO: check question length(<160)
                            //Hope you meant to toast incase the length was greater than 160 characters
                        }
                        else if(questionText.getText().toString().length() > 160){
                            Toast.makeText(EventActivity.this, getResources().getString(R.string.long_question_msg),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //send question
                            SPARQApplication.sendThreadMessage(
                                    ApplicationLayerPdu.TYPE.QUESTION,
                                    SPARQApplication.getBdcastAddress(),
                                    questionText.getText().toString(),
                                    SPARQApplication.getOwnAddress(),
                                    SPARQApplication.getCurrentQuestionId(),
                                    SPARQApplication.getOwnAddress(),
                                    SPARQApplication.getCurrentAnswerId(),
                                    null
                            );

                            Toast.makeText(EventActivity.this, getResources().getString(R.string.new_question),
                                    Toast.LENGTH_SHORT).show();

                            //Re-start timer to disable buttons
                            SPARQInstance.startTimer();
                        }

                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.UI_ENABLE_BROADCAST_INTENT);
            filter.addAction(Constants.UI_DISABLE_BROADCAST_INTENT);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(timerReceiver,filter);
            isReceiverRegistered = true;

            //Checks if the timer has elapsed, if it has the buttons can be active again
            if(SPARQApplication.isTimerElapsed()){
                Log.i(TAG, "onResume: " + SPARQApplication.isTimerElapsed());
                newEvent.setEnabled(true);
            }
            else {
                newEvent.setEnabled(false);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.i(TAG, "onPause");

        if (isReceiverRegistered) {
            unregisterReceiver(timerReceiver);
            isReceiverRegistered = false;
        }
    }

//    public class UIReceiver extends BroadcastReceiver
//    {
//
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//
//        }
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (newEvent.isExpanded()) {

                Rect outRect = new Rect();
                newEvent.getGlobalVisibleRect(outRect);


                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    newEvent.collapse();

            }
        }

        return super.dispatchTouchEvent(event);
    }


}
