package com.sparq.application.layer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.PollPdu;
import com.sparq.application.layer.pdu.ThreadPdu;

import java.io.UnsupportedEncodingException;
import java.util.List;

import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;
import test.com.blootoothtester.network.linklayer.DeviceDiscoveryHandler;
import test.com.blootoothtester.network.linklayer.LinkLayerManager;
import test.com.blootoothtester.network.linklayer.LlContext;
import test.com.blootoothtester.network.linklayer.LlMessage;
import test.com.blootoothtester.util.Logger;

import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE_BYTES;

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

    public boolean sendData(ApplicationLayerPdu.TYPE type, byte[] data, byte toAddr , List<Byte> headers, List<Boolean> flags) {

        switch(type){
            case POLL_QUESTION:
            case POLL_ANSWER:
                if(headers.size() + TYPE_BYTES + PollPdu.FLAG_BYTES == PollPdu.HEADER_POLL_MAX_BYTES
                        && flags.size() == PollPdu.FLAGS_POLL_MAX_BITS){
                    return sendPollData(
                            type,
                            headers.get(0), headers.get(1), headers.get(2), headers.get(3), headers.get(4),
                            flags.get(0), flags.get(1), flags.get(2),
                            data,
                            toAddr
                    );
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header/flag values (Found: " +
                            (headers.size() + TYPE_BYTES + PollPdu.FLAG_BYTES) + " and flags" + flags.size() + " but expected " + PollPdu.HEADER_POLL_MAX_BYTES + " of header and" +
                            PollPdu.FLAGS_POLL_MAX_BITS + " of flags)");
                }

            case QUESTION:
            case ANSWER:
            case QUESTION_VOTE:
            case ANSWER_VOTE:
                if(headers.size() + 1 == ThreadPdu.HEADER_MAX_BYTES){
                    return sendThreadData(type, headers.get(0), headers.get(1), headers.get(2), headers.get(3), data, toAddr);
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            headers.size() + " but expected " + ThreadPdu.HEADER_MAX_BYTES + ")");
                }
        }

        return false;
    }

    public boolean sendThreadData(ApplicationLayerPdu.TYPE type, byte threadCreatorId,
                               byte threadId, byte subThreadCreatorId, byte subThreadId, byte[] data, byte toAddr){


        return mAlContext.sendThreadPdu(type, threadCreatorId, threadId, subThreadCreatorId, subThreadId, data, toAddr);
    }

    public boolean sendPollData(ApplicationLayerPdu.TYPE type, byte pollId,byte questionCreatorId,
                                byte questionId, byte questionFormat, byte answerCreatorId, boolean hasMore, boolean endOfPoll, boolean isMainQuestion,
                                byte[] data, byte toAddr){


        return mAlContext.sendPollPdu(type, pollId,questionCreatorId, questionId, questionFormat,answerCreatorId,
                hasMore, endOfPoll, isMainQuestion, data, toAddr);

    }

    /**
     * called from the USER interface when data is to be sent to the application layer
     * @param type type of data to be sent
     * @param msg actual data
     * @param toAddr address of receiver
     * @param headers set of variables that make up header information
     */
    public boolean sendData(ApplicationLayerPdu.TYPE type, String msg, byte toAddr, List<Byte> headers, List<Boolean> flags) {
        try {
            return sendData(type, msg.getBytes("UTF-8"), toAddr, headers, flags);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
