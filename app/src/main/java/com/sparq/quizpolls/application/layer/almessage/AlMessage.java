package com.sparq.quizpolls.application.layer.almessage;

import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlMessage {

    private ApplicationLayerPdu.TYPE mMessageType;

    public AlMessage(ApplicationLayerPdu.TYPE messageType){
        mMessageType = messageType;
    }


}
