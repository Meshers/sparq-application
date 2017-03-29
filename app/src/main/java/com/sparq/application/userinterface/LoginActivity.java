package com.sparq.application.userinterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sparq.R;
import com.sparq.application.SPARQApplication;

public class LoginActivity extends AppCompatActivity {

    private EditText mEventCode;
    private EditText mOwnAddr;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        initializeViews();

        makePermissionsRequest();
    }

    public void makePermissionsRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {"android.permission.ACCESS_FINE_LOCATION"};
            requestPermissions(
                    permissions, 1
            );
            // this takes care of letting the user add the WRITE_SETTINGS permission
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for(int grantResult: grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                makePermissionsRequest();
                break;
            }
        }
    }

    public void onNextClick() {
        Intent intent = new Intent(this, EventActivity.class);
        String eventCode = mEventCode.getText().toString();
        String addrStr = mOwnAddr.getText().toString();

        //TODO: check if eventCode and addrStr are valid numbers < 127

        if (eventCode.equals("") || addrStr.equals("")) {
            return;
        }

        SPARQApplication.setOwnAddr(Byte.parseByte(addrStr));
        SPARQApplication.setSessionId(Byte.parseByte(eventCode));
        SPARQApplication.initializeObjects(LoginActivity.this);

        intent.putExtra(Main2Activity.EXTRA_EVENT_CODE, Byte.parseByte(addrStr));

        startActivity(intent);
    }

    public void initializeViews(){

        mEventCode = (EditText) findViewById(R.id.event_code_input);
        mOwnAddr = (EditText) findViewById(R.id.own_addr_input);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick();
            }
        });

    }

}
