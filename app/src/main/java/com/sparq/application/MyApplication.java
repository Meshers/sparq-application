package com.sparq.application;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.almessage.AlQuestion;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.Main2Activity;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

/**
 * Created by sarahcs on 3/26/2017.
 */

public class MyApplication extends Application {
    static MyBluetoothAdapter mBluetoothAdapter;
    static ApplicationPacketDiscoveryHandler mHandler;
    static ApplicationLayerManager mManager;
    private static byte mSessionId = (byte) 1;
    private static byte mOwnAddr = (byte) 1;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public static byte getOwnAddress(){
        return mOwnAddr;
    }

    public static void setOwnAddr(byte ownAddr){
        mOwnAddr = ownAddr;
    }

    public static byte getSessionId(){
        return mSessionId;
    }

    public static void setSessionId(byte sessionId){
        mSessionId = sessionId;
    }

    public static void initializeObjects(Activity activity){



    }

    public static void writeToDb(ApplicationLayerPdu.TYPE type, AlMessage alMessage){
        switch(type){
            case QUESTION:

                AlQuestion alQuestion = (AlQuestion) alMessage;

                break;
            case ANSWER:
                break;
            case QUESTION_VOTE:
                break;
            case ANSWER_VOTE:
                break;
            default:
                throw new IllegalArgumentException("Illegal message type.");
        }
    }

    public static MyBluetoothAdapter getBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    public static ApplicationPacketDiscoveryHandler getHandler(){
        return mHandler;
    }

    public static ApplicationLayerManager getApplicationLayerManager(){
        return mManager;
    }

}
