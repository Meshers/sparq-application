package com.sparq.application.layer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

    private BroadcastReceiver mBroadcastReceiver;
    private ApplicationPacketDiscoveryHandler mApplicationPacketDiscoveryHandler;
    private AlContext mAlContext;
    private byte mToAddr;
    private byte mOwnAddr;
    private LinkLayerManager mLinkLayerManager;

    public ApplicationLayerManager(byte ownAddr,
                                   final LinkLayerManager linkLayerManager,
                                   final ApplicationPacketDiscoveryHandler applicationPacketDiscoveryHandler){

        this.mApplicationPacketDiscoveryHandler = applicationPacketDiscoveryHandler;
        this.mLinkLayerManager = linkLayerManager;
        this.mOwnAddr = ownAddr;


        AlContext.Callback callback = new AlContext.Callback() {
            @Override
            public void transmitPdu(ApplicationLayerPdu pdu) {
                mLinkLayerManager.sendData(pdu.encode(), mToAddr);
            }

            @Override
            public void sendUpperLayer(AlMessage message) {
                applicationPacketDiscoveryHandler.handleDiscovery(message);
            }
        };

        DeviceDiscoveryHandler discoveryHandler = new DeviceDiscoveryHandler() {

            @Override
            public void handleDiscovery(LlMessage llMessage) {

                ApplicationLayerPdu pdu = ThreadPdu.from(llMessage);
                mAlContext.receivePdu(pdu);
            }
        };
    }

    public void sendData(ApplicationLayerPdu.TYPE type, byte[] data, byte toAddr , byte... headers) {

        switch(type){
            case QUESTION:
            case ANSWER:
            case QUESTION_VOTE:
            case ANSWER_VOTE:
                if(headers.length != ThreadPdu.HEADER_MAX_BYTES){
                    sendThreadData(type, headers[0], headers[1], headers[2], headers[3], data);
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            headers.length + " but expected " + ThreadPdu.HEADER_MAX_BYTES + ")");
                }
                break;
        }
    }

    public void sendThreadData(ApplicationLayerPdu.TYPE type, byte threadCreatorId,
                               byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data){


        mAlContext.sendThreadPdu(type, threadCreatorId, threadId, subThreadCreatorId, subThreadId, data);
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
