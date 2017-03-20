package com.sparq.application.layer;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.ThreadPdu;
import com.sparq.application.userinterface.model.ConversationThread;

import java.util.ArrayList;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlContext {
    private static final int SESSION_ID = 1;

    private boolean mBusy;
    private ApplicationLayerPdu mCurrentPdu;
    private Callback mCallback;
    private long timeSinceLastMessage;
    private ArrayList<ConversationThread> mThreads;

    public interface Callback {
        void transmitPdu(ApplicationLayerPdu pdu);

       // void sendUpperLayer(LlMessage message);
    }

    private final byte mSessionId;

    public AlContext(byte sessionId, Callback callback){
        this.mSessionId = SESSION_ID;
        this.mCallback = callback;

        mThreads = new ArrayList<>();

    }

    public void sendPdu(ApplicationLayerPdu pdu){

        timeSinceLastMessage = System.currentTimeMillis();
        mBusy = true;
        sendPduToLowerLayer(pdu);
    }

    private void sendPduToLowerLayer(ApplicationLayerPdu pdu) {
        mCurrentPdu = pdu;
        mCallback.transmitPdu(pdu);
    }

}
