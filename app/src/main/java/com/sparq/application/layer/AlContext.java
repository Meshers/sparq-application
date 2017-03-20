package com.sparq.application.layer;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.layer.pdu.ThreadPdu;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlContext {

    private static final int SESSION_ID = 1;

    public interface Callback {
        void transmitPdu(ApplicationLayerPdu pdu);

       // void sendUpperLayer(LlMessage message);
    }

    private final byte mSessionId;

    public AlContext(byte sessionId){
        this.mSessionId = SESSION_ID;

    }

    public void sendPdu(ApplicationLayerPdu.TYPE type, byte toId, byte[] data){

        ApplicationLayerPdu pdu;

        switch(type){
            case QUESTION:
                pdu = ThreadPdu.getQuestionPdu()
                break;
            case ANSWER:
                break;
            case QUESTION_VOTE:
                break;
            case ANSWER_VOTE:
                break;
        }

        mLastOwnMessageTime = System.currentTimeMillis();
        mBusy = true;
        sendPduToLowerLayer(pdu);
    }


}
