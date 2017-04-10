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
import com.sparq.application.layer.pdu.WifiBTQuizPdu;
import com.sparq.util.Constants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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

                ApplicationLayerPdu pdu = null;

                if(llMessage.getDataAsString().equalsIgnoreCase("init")){
                    return;
                }

                switch(ApplicationLayerPdu.getTypeDecoded(llMessage.getData()[0])){
                    case QUIZ_QUESTION:
                    case QUIZ_ANSWER:
                        pdu = WifiBTQuizPdu.from(llMessage);
                        break;
                    case POLL_QUESTION:
                    case POLL_ANSWER:
                        pdu = PollPdu.from(llMessage);
                        break;
                    case QUESTION:
                    case ANSWER:
                    case QUESTION_VOTE:
                    case ANSWER_VOTE:
                        pdu = ThreadPdu.from(llMessage);
                        break;
                }
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

    public boolean sendData(ApplicationLayerPdu.TYPE type, byte[] data, byte[][] options,byte toAddr , List<Byte> headers, List<Boolean> flags) {

        Log.i("HERE", "send data");
        switch(type){
            case QUIZ_QUESTION:
            case QUIZ_ANSWER:
                if(headers.size() + 1 <= WifiBTQuizPdu.HEADER_QUIZ_MAX_BYTES){
                    Log.i("HERE", "checked headers");
                    return sendQuizData(type, headers.get(0), headers.get(1), headers.get(2), headers.get(3), data, toAddr);
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            headers.size() + " but expected " + WifiBTQuizPdu.HEADER_QUIZ_MAX_BYTES + ")");
                }

            case POLL_QUESTION:
                if(headers.size() + TYPE_BYTES + PollPdu.FLAG_BYTES + PollPdu.HEADER_SIZE_BYTES - PollPdu.ANSWER_CREATOR_ID_BYTES == PollPdu.PDU_POLL_QUESTION_HEADER_MIN_BYTES
                        && flags.size() == PollPdu.FLAGS_POLL_MAX_BITS){
                    return sendPollData(
                            type,
                            headers.get(0), headers.get(1), headers.get(2), headers.get(3), headers.get(4),
                            flags.get(0), flags.get(1), flags.get(2),
                            data,
                            options,
                            toAddr
                    );
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header/flag values (Found: " +
                            (headers.size() + TYPE_BYTES + PollPdu.FLAG_BYTES + PollPdu.HEADER_SIZE_BYTES - PollPdu.ANSWER_CREATOR_ID_BYTES)
                            + " and flags " + flags.size() + " but expected " + PollPdu.PDU_POLL_QUESTION_HEADER_MIN_BYTES + " of header and " +
                            PollPdu.FLAG_BYTES + " of flags)");
                }

            case POLL_ANSWER:
                if(headers.size() + TYPE_BYTES == PollPdu.PDU_POLL_ANSWER_HEADER_BYTES
                        && flags.size() == PollPdu.FLAGS_POLL_MAX_BITS){
                    return sendPollData(
                            type,
                            headers.get(0), headers.get(1), headers.get(2), headers.get(3), headers.get(4),
                            flags.get(0), flags.get(1), flags.get(2),
                            data,
                            options,
                            toAddr
                    );
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            (headers.size() + TYPE_BYTES) + " and flags " + flags.size() + " but expected " + PollPdu.PDU_POLL_ANSWER_HEADER_BYTES + " of headers)");
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
                                byte[] data, byte[][] options, byte toAddr){

        return mAlContext.sendPollPdu(type, pollId, questionCreatorId, questionId, questionFormat,answerCreatorId,
                hasMore, endOfPoll, isMainQuestion, data, options,toAddr);

    }

    public boolean sendQuizData(ApplicationLayerPdu.TYPE type, byte quizId,
                                byte questionFormat, byte numberOfQuestios, byte answerCreatorId,
                                byte[] data, byte toAddr){

        Log.i("HERE", "send Quiz Data");
        return mAlContext.sendQuizPdu(type, quizId, questionFormat, numberOfQuestios,answerCreatorId, data, toAddr);

    }

    /**
     * called from the USER interface when data is to be sent to the application layer
     * @param type type of data to be sent
     * @param msg actual data
     * @param toAddr address of receiver
     * @param headers set of variables that make up header information
     */
    public boolean sendData(ApplicationLayerPdu.TYPE type, String msg, List<String> options, byte toAddr, List<Byte> headers, List<Boolean> flags) {
        try {
            byte[][] optionsArray = null;
            if(options != null){
                optionsArray = new byte[options.size()][];
                for(int i = 0; i < options.size(); i++){
                    optionsArray[i] = options.get(i).getBytes("UTF-8");
                }
            }

            return sendData(type, msg.getBytes("UTF-8"),optionsArray, toAddr, headers, flags);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendBundledData(ApplicationLayerPdu.TYPE type, HashMap<Integer, String> msg, byte toAddr, List<Byte> headers, List<Boolean> flags) {
        try {

            Log.i("HERE", "send bundle message");
            String bundledMessage = "";

            for(int questionNumber: msg.keySet()){
                bundledMessage += msg.get(questionNumber);
            }

            Log.i("HERE", "bundle message" + bundledMessage);

            return sendData(type, bundledMessage.getBytes("UTF-8"), null, toAddr, headers, flags);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
