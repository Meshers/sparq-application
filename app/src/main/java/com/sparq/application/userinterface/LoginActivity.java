package com.sparq.application.userinterface;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.sparq.R;
import com.sparq.application.SPARQApplication;

public class LoginActivity extends AppCompatActivity {

    private EditText mEventCode;
    private EditText mOwnAddr;
    private Button submit;
    private TextInputLayout layout;

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

        openDialog();
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

    public void openDialog(){
        final MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
                .theme(Theme.LIGHT)
                .customView(R.layout.dialog_user_identifier, true)
                .positiveText("Login as Student")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        SPARQApplication.setIsTeacher(false);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if(SPARQApplication.isTeacher()){
                            layout.setVisibility(View.GONE);
                        }else{
                            layout.setVisibility(View.VISIBLE);
                        }
                    }
                }).build();

        View v = dialog.getCustomView();

        final PinEntryEditText pinEntry = (PinEntryEditText) v.findViewById(R.id.txt_pin_entry);


        pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                if (str.toString().equals("1234")) {
                    Toast.makeText(LoginActivity.this, "Authenticated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    SPARQApplication.setOwnAddr((byte) 1);
                    SPARQApplication.setIsTeacher(true);

                } else {
                    Toast.makeText(LoginActivity.this, "Failed to authenticate", Toast.LENGTH_SHORT).show();
                    pinEntry.setText(null);
                    SPARQApplication.setIsTeacher(false);
                }
            }
        });

        dialog.show();
    }

    public void onNextClick() {
        Intent intent = new Intent(this, EventActivity.class);
        String eventCode = mEventCode.getText().toString();
        String ownAddress = mOwnAddr.getText().toString();


        //TODO: check if eventCode and addrStr are valid numbers < 127
        //Fixed for now but we need to decide what exactly goes here for ownAddress

        if(Integer.valueOf(eventCode) > 127){
            Toast.makeText(LoginActivity.this, "Invalid Event Code", Toast.LENGTH_SHORT).show();
            return;
        }

        // FIXME: 29/3/17 on change of link layer logic
        // Handles arrayindexoutofbounds exception in case ownAddress more than 40
        if(Integer.valueOf(ownAddress) > 40){
            Toast.makeText(LoginActivity.this, "Invalid address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(eventCode.equals("") || ownAddress.equals("")){
            Toast.makeText(LoginActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SPARQApplication.setSessionId(Byte.parseByte(eventCode));

        if(!SPARQApplication.isTeacher()){

            String addrStr = mOwnAddr.getText().toString();
            if(addrStr.equals("")){
                return;
            }
            SPARQApplication.setOwnAddr(Byte.parseByte(addrStr));
            intent.putExtra(Main2Activity.EXTRA_EVENT_CODE, SPARQApplication.getOwnAddress());

        }


        SPARQApplication.initializeObjects(LoginActivity.this);

        startActivity(intent);
    }

    public void initializeViews(){

        mEventCode = (EditText) findViewById(R.id.event_code_input);
        mOwnAddr = (EditText) findViewById(R.id.own_addr_input);
        layout = (TextInputLayout) findViewById(R.id.own_addr);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onNextClick();
            }
        });

    }

}
