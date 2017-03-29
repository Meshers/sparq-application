package com.sparq.application.userinterface;

import android.os.Bundle;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.adapter.EventPagerAdapter;
import com.sparq.application.userinterface.model.UserItem;
import com.sparq.util.Constants;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

public class EventActivity extends AppCompatActivity {

    TabLayout tabLayout;
    FloatingActionsMenu newEvent;
    FloatingActionButton newConvThread;

    private MyBluetoothAdapter mBluetoothAdapter;
    private ApplicationPacketDiscoveryHandler mHandler;
    private ApplicationLayerManager mManager;

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

        mBluetoothAdapter = new MyBluetoothAdapter(EventActivity.this);

        mHandler = new ApplicationPacketDiscoveryHandler() {
            @Override
            public void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage message) {
                SPARQApplication.handlePackets(type, message);
            }
        };

        mManager = new ApplicationLayerManager(SPARQApplication.getOwnAddress(), mBluetoothAdapter, mHandler,SPARQApplication.getSessionId());

        SPARQApplication.setApplicationLayerManager(mManager);

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
    }

    public void initializeViews(){

        newEvent = (FloatingActionsMenu) findViewById(R.id.fab_new_event);
        newConvThread = (FloatingActionButton) findViewById(R.id.fab_new_thread);

        newConvThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
                        }
                        else{
                            //send question
                            SPARQApplication.sendMessage(
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

}
