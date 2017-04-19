package com.sparq.quizpolls.application.userinterface;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.SPARQApplication;
import com.sparq.quizpolls.application.layer.ApplicationLayerManager;
import com.sparq.quizpolls.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.quizpolls.application.layer.almessage.AlMessage;
import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.quizpolls.application.userinterface.adapter.EventPagerAdapter;
import com.sparq.quizpolls.application.userinterface.model.Questionare;
import com.sparq.quizpolls.util.Constants;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

import static com.sparq.quizpolls.application.SPARQApplication.getInstance;
import static com.sparq.quizpolls.application.SPARQApplication.getUserType;

//import android.support.design.widget.FloatingActionButton;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";
    public final static String EXTRA_EVENT_CODE = "EVENT_CODE";

    boolean doubleBackToExitPressedOnce = false;

    TabLayout tabLayout;
    public FloatingActionsMenu newEvent;
    FloatingActionButton newQuiz ,newPoll, newConvThread;

    private MyBluetoothAdapter mBluetoothAdapter;
    private ApplicationPacketDiscoveryHandler mHandler;
    private ApplicationLayerManager mManager;

    //broadcast receiver
    BroadcastReceiver timerReceiver;
    boolean isReceiverRegistered;

    private ProgressDialog mBtResponseDialog;

    public static EventActivity mEventActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventActivity = this;
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        int eventId = bundle.getInt("event_id");

        // FIXME: 4/9/2017 
        // add event name here
        setTitle("SPARQ");

        initializeViews();

        initializeLowerLayer();

        tabLayout.addTab(tabLayout.newTab().setText("Quiz"));
        tabLayout.addTab(tabLayout.newTab().setText("Poll"));
//        tabLayout.addTab(tabLayout.newTab().setText("Forums"));
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

        SPARQApplication.USER_TYPE userType = getUserType();

        if(userType == null){
            SPARQApplication.setUserType(SPARQApplication.USER_TYPE.STUDENT);
            userType = SPARQApplication.USER_TYPE.STUDENT;
        }

        newEvent = (FloatingActionsMenu) findViewById(R.id.fab);
        newPoll = (FloatingActionButton) findViewById(R.id.fab_new_poll);
        newQuiz = (FloatingActionButton) findViewById(R.id.fab_new_quiz);
        newConvThread = (FloatingActionButton) findViewById(R.id.fab_new_thread);

        newConvThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
                newEvent.collapse();
            }
        });

        switch (userType){
            case TEACHER:
                newPoll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventActivity.this, NewQuestionareActivity.class);
                        intent.putExtra(NewQuestionareActivity.QUESTIONARE_TYPE, Questionare.QUESTIONARE_TYPE.POLL);
                        startActivity(intent);
                        newEvent.collapse();

                    }
                });

                newQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventActivity.this, NewQuestionareActivity.class);
                        intent.putExtra(NewQuestionareActivity.QUESTIONARE_TYPE, Questionare.QUESTIONARE_TYPE.QUIZ);
                        startActivity(intent);
                        newEvent.collapse();
                    }
                });

                break;
            case STUDENT:
                newPoll.setVisibility(View.GONE);
                newQuiz.setVisibility(View.GONE);
                break;
            default:
                throw new IllegalArgumentException("Illegal user type.");
        }

        newConvThread.setVisibility(View.GONE);

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

        mManager = new ApplicationLayerManager(EventActivity.this, SPARQApplication.getOwnAddress(), mBluetoothAdapter, mHandler,SPARQApplication.getSessionId());

        SPARQApplication.setApplicationLayerManager(mManager);
    }

    public void openDialog(){

        MaterialDialog dialog = new MaterialDialog.Builder(EventActivity.this)
                .title(getResources().getString(R.string.new_question))
                .customView(R.layout.dialog_new_thread_question, true)
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
                            getInstance().startTimer();
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
        }

        //Checks if the timer has elapsed, if it has the buttons can be active again
        if(SPARQApplication.isTimerElapsed()){
            newEvent.setEnabled(true);
        }
        else {
            newEvent.setEnabled(false);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if (isReceiverRegistered) {
            unregisterReceiver(timerReceiver);
            isReceiverRegistered = false;
        }
    }

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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to leave", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void showBtResponseProgressDialog() {
        mBtResponseDialog = new ProgressDialog(this);
        mBtResponseDialog.setMessage("Your answer is being submitted");
        mBtResponseDialog.setTitle("Please Wait");
        mBtResponseDialog.setCancelable(false);
        mBtResponseDialog.show();
    }

    public void hideBtResponseProgressDialog() {
        if (mBtResponseDialog != null) {
            mBtResponseDialog.hide();
            new AlertDialog.Builder(this).setTitle("Success!")
                    .setMessage("Your answer has been submitted and acknowledged")
                    .show();
        }
    }

}
