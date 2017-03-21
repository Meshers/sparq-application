package com.sparq.application.layer;

import android.content.BroadcastReceiver;

import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.io.UnsupportedEncodingException;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;
import test.com.blootoothtester.network.linklayer.DeviceDiscoveryHandler;
import test.com.blootoothtester.network.linklayer.LinkLayerManager;
import test.com.blootoothtester.network.linklayer.LlContext;
import test.com.blootoothtester.util.Logger;

/**
 * This class contains information about the current context in which the LinkLayer is running such
 * as messages received so far, session ID, etc.
 */

public class ApplicationLayerManager {
    private MyBluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private DeviceDiscoveryHandler mDiscoveryHandler;
    private AlContext mAlContext;
    private LinkLayerManager mLinkLayerManager;
    private Logger mLogger = new Logger();
    private LlContext mLlContext;

    public ApplicationLayerManager(LinkLayerManager linkLayerManager, MyBluetoothAdapter bluetoothAdapter,
                                   DeviceDiscoveryHandler discoveryHandler){
        mLinkLayerManager = linkLayerManager;
        mBluetoothAdapter = bluetoothAdapter;
        mDiscoveryHandler = discoveryHandler;

        //I'm sorry
        AlContext.Callback callback = new AlContext.Callback() {
            @Override
            public void transmitPdu(ApplicationLayerPdu pdu) {
                // FIXME: 22/3/17
                // mLogger.d("ApplicationLayerManager", "sendData: seq.id: " + pdu.getSequenceId());
                // mLinkLayerManager.sendData();
            }

            @Override
            // FIXME: 22/3/17 Need to decide how to handle AlMessage
            public void sendUpperLayer(AlMessage message) {
                mDiscoveryHandler.handleDiscovery(message);
            }
        }
    }

    // FIXME: 22/3/17 when AlContext is built
    public void sendData(byte[] packet, byte type) {
        mAlContext.sendPdu(type, packet);
    }

    public void sendData(String msg, byte toAddr) {
        try {
            sendData(msg.getBytes("UTF-8"), toAddr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToLinkLayer(String msg, Byte toId) {
        mLinkLayerManager.sendData(msg, toId);
    }

    public String getMessageFromLinkLayer(){
        
        //Don't know how to use that callback
    }

}
