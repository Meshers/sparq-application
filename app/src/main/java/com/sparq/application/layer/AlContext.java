package com.sparq.application.layer;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlContext {

    public interface Callback {
        void transmitPdu(ApplicationLayerPdu pdu);

       // void sendUpperLayer(LlMessage message);
    }

    private final byte sessionId;


}
