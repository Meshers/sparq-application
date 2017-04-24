package com.sparq.application.layer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.ThreadPdu;
import com.sparq.application.layer.pdu.WifiBTQuestionarePdu;
import com.sparq.application.userinterface.EventActivity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;


import test.com.blootoothtester.bluetooth.MyBluetoothAdapter;
import test.com.blootoothtester.network.linklayer.DeviceDiscoveryHandler;
import test.com.blootoothtester.network.linklayer.LinkLayerManager;
import test.com.blootoothtester.network.linklayer.LlMessage;

import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE.POLL_ANSWER;
import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE.POLL_QUESTION;
import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE.QUIZ_ANSWER;
import static com.sparq.application.layer.pdu.ApplicationLayerPdu.TYPE.QUIZ_QUESTION;

/**
 * This class contains information about the current context in which the ApplicatonLayer is running
 */

public class ApplicationLayerManager {

    private byte mSessionId = (byte) 1;

    Context mContext;

    private BroadcastReceiver mBroadcastReceiver;
    private ApplicationPacketDiscoveryHandler mApplicationPacketDiscoveryHandler;
    private AlContext mAlContext;
    private byte mOwnAddr;
    private LinkLayerManager mLinkLayerManager;
//    private WifiLlManager mWifiLlManager;

    public ApplicationLayerManager(Context context, byte ownAddr, MyBluetoothAdapter bluetoothAdapter,
                                   final ApplicationPacketDiscoveryHandler applicationPacketDiscoveryHandler, byte sessionId){

        this.mContext = context;

        this.mApplicationPacketDiscoveryHandler = applicationPacketDiscoveryHandler;
        this.mOwnAddr = ownAddr;
        this.mSessionId = sessionId;

        initializeArchitectureOne(bluetoothAdapter);
//        initializeArchitectureTwo(bluetoothAdapter);

        AlContext.Callback callback = new AlContext.Callback() {
            @Override
            public void transmitPdu(ApplicationLayerPdu pdu, byte toAddr) {

                switch(pdu.getType()){
//                    case QUIZ_QUESTION:
//                    case POLL_QUESTION:
//                        mWifiLlManager.sendWifiMessage(pdu.encode());
//                        break;
//                    case QUIZ_ANSWER:
//                    case POLL_ANSWER:
//                        mWifiLlManager.sendBtMessage(
//                                ((WifiBTQuestionarePdu) pdu).getToAddr(),
//                                ((WifiBTQuestionarePdu) pdu).getLinkId(),
//                                pdu.encode()
//                        );
//                        break;
                    case QUESTION:
                    case ANSWER:
                    case ANSWER_VOTE:
                    case QUESTION_VOTE:
                        mLinkLayerManager.sendData(
                                new String(pdu.encode(), Charset.forName("UTF-8")),
                                toAddr
                        );
                        break;

                }
            }

            @Override
            public void sendUpperLayer(ApplicationLayerPdu.TYPE type, AlMessage message) {
                mApplicationPacketDiscoveryHandler.handleDiscovery(type, message);
            }
        };

        this.mAlContext = new AlContext(mSessionId, callback);
        mLinkLayerManager.startReceiving();

//        mWifiLlManager.startReceivingWifiMessages();

    }

    public void initializeArchitectureOne(MyBluetoothAdapter bluetoothAdapter){

        DeviceDiscoveryHandler discoveryHandler = new DeviceDiscoveryHandler() {

            @Override
            public void handleDiscovery(LlMessage llMessage) {
                Log.i("LLMSSG","Message Received from linklayer: "+ llMessage.getDataAsString());

                ApplicationLayerPdu pdu = null;

                if(llMessage.getDataAsString().equalsIgnoreCase("init")){
                    return;
                }

                switch(ApplicationLayerPdu.getTypeDecoded(llMessage.getData()[0])){
                    // this callback is used only for application layer thread PDUs
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
                SPARQApplication.getSessionId(),
                bluetoothAdapter,
                discoveryHandler
        );
    }

//    public void initializeArchitectureTwo(MyBluetoothAdapter bluetoothAdapter){
//
//        mWifiLlManager = new WifiLlManager(
//                mContext,
//                SPARQApplication.getOwnAddress(),
//                mSessionId,
//                new WifiLlManager.MessageCallback() {
//
//                    ApplicationLayerPdu pdu = null;
//
//                    @Override
//                    public void onReceiveWifiMessage(WifiMessage wifiMessage) {
//                        // teacher -> student
//                        // poll questions and quiz questions
//                        switch(ApplicationLayerPdu.getTypeDecoded(wifiMessage.getBody()[0])) {
//                            case QUIZ_QUESTION:
//                                pdu = WifiBTQuestionarePdu.from(wifiMessage);
//                                break;
//                            case POLL_QUESTION:
////                              pdu = PollPdu.from(llMessage);
//                                pdu = WifiBTQuestionarePdu.from(wifiMessage);
//                                break;
//                            default:
//                                throw new IllegalArgumentException(
//                                        "Invalid Packet type. Found " +
//                                        ApplicationLayerPdu.getTypeDecoded(wifiMessage.getBody()[0]) +
//                                        " but expected " + QUIZ_QUESTION +
//                                        " or " + POLL_QUESTION
//                                );
//                        }
//
//                        if(pdu != null){
//                            mAlContext.receivePdu(pdu);
//                        }
//                    }
//
//                    @Override
//                    public void onReceiveBtMessage(BtMessage btMessage) {
//                        // student -> teacher
//                        // poll answers and quiz answers
//                        switch(ApplicationLayerPdu.getTypeDecoded(btMessage.getBody()[0])) {
//                            case QUIZ_ANSWER:
//                                pdu = WifiBTQuestionarePdu.from(btMessage);
//                                break;
//                            case POLL_ANSWER:
////                              pdu = PollPdu.from(llMessage);
//                                pdu = WifiBTQuestionarePdu.from(btMessage);
//                                break;
//                            default:
//                                throw new IllegalArgumentException(
//                                        "Invalid Packet type. Found " +
//                                                ApplicationLayerPdu.getTypeDecoded(btMessage.getBody()[0]) +
//                                                " but expected " + QUIZ_ANSWER +
//                                                " or " + POLL_ANSWER
//                                );
//                        }
//
//                        if(pdu != null){
//                            mAlContext.receivePdu(pdu);
//                        }
//                    }
//
//                    @Override
//                    public void onAckedByWifi() {
//                        if (EventActivity.mEventActivity != null) {
//                            EventActivity.mEventActivity.hideBtResponseProgressDialog();
//                        }
//                    }
//                },
//                bluetoothAdapter
//        );
//    }

    public boolean sendData(ApplicationLayerPdu.TYPE type, byte[] data, byte[][] options,byte toAddr , List<Byte> headers, List<Boolean> flags) {

        switch(type){
            case QUIZ_QUESTION:
            case QUIZ_ANSWER:
                if(headers.size() + 1 <= WifiBTQuestionarePdu.HEADER_QUIZ_MAX_BYTES){
                    return sendQuizData(type, headers.get(0), headers.get(1), headers.get(2), headers.get(3), data, toAddr);
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            headers.size() + " but expected " + WifiBTQuestionarePdu.HEADER_QUIZ_MAX_BYTES + ")");
                }

            case POLL_QUESTION:
//                if(headers.size() + TYPE_BYTES + PollPdu.FLAG_BYTES + PollPdu.HEADER_SIZE_BYTES - PollPdu.ANSWER_CREATOR_ID_BYTES == PollPdu.PDU_POLL_QUESTION_HEADER_MIN_BYTES
//                        && flags.size() == PollPdu.FLAGS_POLL_MAX_BITS){
//                    return sendPollData(
//                            type,
//                            headers.get(0), headers.get(1), headers.get(2), headers.get(3), headers.get(4),
//                            flags.get(0), flags.get(1), flags.get(2),
//                            data,
//                            options,
//                            toAddr
//                    );
//                }
//                else{
//                    throw new IllegalArgumentException("Illegal number of header/flag values (Found: " +
//                            (headers.size() + TYPE_BYTES + PollPdu.FLAG_BYTES + PollPdu.HEADER_SIZE_BYTES - PollPdu.ANSWER_CREATOR_ID_BYTES)
//                            + " and flags " + flags.size() + " but expected " + PollPdu.PDU_POLL_QUESTION_HEADER_MIN_BYTES + " of header and " +
//                            PollPdu.FLAG_BYTES + " of flags)");
//                }

            case POLL_ANSWER:
//                if(headers.size() + TYPE_BYTES == PollPdu.PDU_POLL_ANSWER_HEADER_BYTES
//                        && flags.size() == PollPdu.FLAGS_POLL_MAX_BITS){
//                    return sendPollData(
//                            type,
//                            headers.get(0), headers.get(1), headers.get(2), headers.get(3), headers.get(4),
//                            flags.get(0), flags.get(1), flags.get(2),
//                            data,
//                            options,
//                            toAddr
//                    );
//                }
//                else{
//                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
//                            (headers.size() + TYPE_BYTES) + " and flags " + flags.size() + " but expected " + PollPdu.PDU_POLL_ANSWER_HEADER_BYTES + " of headers)");
//                }

                if(headers.size() + 1 <= WifiBTQuestionarePdu.HEADER_QUIZ_MAX_BYTES){
                    return sendPollData(type, headers.get(0), headers.get(1), headers.get(2), headers.get(3), data, toAddr);
                }
                else{
                    throw new IllegalArgumentException("Illegal number of header values (Found: " +
                            headers.size() + " but expected " + WifiBTQuestionarePdu.HEADER_QUIZ_MAX_BYTES + ")");
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

//    public boolean sendPollData(ApplicationLayerPdu.TYPE type, byte pollId,byte questionCreatorId,
//                                byte questionId, byte questionFormat, byte answerCreatorId, boolean hasMore, boolean endOfPoll, boolean isMainQuestion,
//                                byte[] data, byte[][] options, byte toAddr){
//
//        return mAlContext.sendPollPdu(type, pollId, questionCreatorId, questionId, questionFormat,answerCreatorId,
//                hasMore, endOfPoll, isMainQuestion, data, options,toAddr);
//
//    }

    public boolean sendPollData(ApplicationLayerPdu.TYPE type, byte pollId,
                                byte questionFormat, byte numberOfQuestios, byte answerCreatorId,
                                byte[] data, byte toAddr){

        return mAlContext.sendPollPdu(type, pollId, questionFormat, numberOfQuestios,answerCreatorId, data, toAddr);

    }

    public boolean sendQuizData(ApplicationLayerPdu.TYPE type, byte quizId,
                                byte questionFormat, byte numberOfQuestios, byte answerCreatorId,
                                byte[] data, byte toAddr){

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

            String bundledMessage = "";

            for(int questionNumber: msg.keySet()){
                bundledMessage += msg.get(questionNumber);
            }

            return sendData(type, bundledMessage.getBytes("UTF-8"), null, toAddr, headers, flags);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
