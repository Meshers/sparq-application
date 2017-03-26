package com.sparq.application.layer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.ThreadPdu;

import java.io.UnsupportedEncodingException;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;
import test.com.blootoothtester.network.linklayer.DeviceDiscoveryHandler;
import test.com.blootoothtester.network.linklayer.LinkLayerManager;
import test.com.blootoothtester.network.linklayer.LlContext;
import test.com.blootoothtester.network.linklayer.LlMessage;
import test.com.blootoothtester.util.Logger;

/**
 * This class contains information about the current context in which the ApplicatonLayer is running
 */

public class ApplicationLayerManager {

    private byte mSessionId = (byte) 1;

    private BroadcastReceiver mBroadcastReceiver;
    private ApplicationPacketDiscoveryHandler mApplicationPacketDiscoveryHandler;
    private AlContext mAlContext;
    private byte mOwnAddr;
    private LinkLayerManager mLinkLayerManager;

    public ApplicationLayerManager(byte ownAddr, MyBluetoothAdapter bluetoothAdapter,
                                   final ApplicationPacketDiscoveryHandler applicationPacketDiscoveryHandler, byte sessionId){

        this.mApplicationPacketDiscoveryHandler = applicationPacketDiscoveryHandler;
        this.mOwnAddr = ownAddr;
        this.mSessionId = sessionId;

        DeviceDiscoveryHandler discoveryHandler = new DeviceDiscoveryHandler() {

            @Override
            public void handleDiscovery(LlMessage llMessage) {
                Log.i("LLMSSG","Message Received from linklayer: "+ llMessage.getDataAsString());
                ApplicationLayerPdu pdu = ThreadPdu.from(llMessage);
                if(pdu != null){
                    mAlContext.receivePdu(pdu);
                }
            }
        };

        this.mLinkLayerManager = new LinkLayerManager(
                mOwnAddr,
                bluetoothAdapter,
                discoveryHandler
        );

        AlContext.Callback callback = new AlContext.Callback() {
            @Override
            public void transmitPdu(ApplicationLayerPdu pdu, byte toAddr) {
                mLinkLayerManager.sendData(pdu.encode(), toAddr);
            }

            @Override
            public void sendUpperLayer(ApplicationLayerPdu.TYPE type, AlMessage message) {
                mApplicationPacketDiscoveryHandler.handleDiscovery(type, message);
            }
        };

        this.mAlContext = new AlContext(mSessionId, callback);
        mLinkLayerManager.startReceiving();

    }

    public void sendData(ApplicationLayerPdu.TYPE type, byte[] data, byte toAddr , byte... headers) {

        switch(type){
            case QUESTION:
            case ANSWER:
            case QUESTION_VOTE:
            case ANSWER_VOTE:
                if(headers.length + 1 == ThreadPdu.HEADER_MAX_BYTES){
                    sendThreadData(type, headers[0], headers[1], headers[2], headers[3], data, toAddr);
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            headers.length + " but expected " + ThreadPdu.HEADER_MAX_BYTES + ")");
                }
                break;
        }
    }

    public void sendThreadData(ApplicationLayerPdu.TYPE type, byte threadCreatorId,
                               byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data, byte toAddr){


        mAlContext.sendThreadPdu(type, threadCreatorId, threadId, subThreadCreatorId, subThreadId, data, toAddr);
    }

    /**
     * called from the USER interface when data is to be sent to the application layer
     * @param type type of data to be sent
     * @param msg actual data
     * @param toAddr address of receiver
     * @param headers set of variables that make up header information
     */
    public void sendData(ApplicationLayerPdu.TYPE type, String msg, byte toAddr, byte... headers) {
        try {
            sendData(type, msg.getBytes("UTF-8"), toAddr, headers);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
