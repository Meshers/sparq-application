package com.sparq.application;

import android.app.Application;

import com.sparq.application.layer.ApplicationLayerManager;
import com.sparq.application.layer.ApplicationPacketDiscoveryHandler;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;

/**
 * Created by sarahcs on 3/26/2017.
 */

public class MyApplication extends Application {
    MyBluetoothAdapter mBluetoothAdapter;
    ApplicationPacketDiscoveryHandler mHandler;
    ApplicationLayerManager mManager;
    private byte mOwnAddr = (byte) 1;

    @Override
    public void onCreate(){
        super.onCreate();

//        mBluetoothAdapter = new MyBluetoothAdapter();

        mHandler = new ApplicationPacketDiscoveryHandler() {
            @Override
            public void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage alMessage) {



            }
        };

        mManager = new ApplicationLayerManager(mOwnAddr, mBluetoothAdapter, mHandler);
    }

    public byte getOwnAddress(){
        return mOwnAddr;
    }

    public void setOwnAddr(byte ownAddr){
        mOwnAddr = ownAddr;
    }

}
