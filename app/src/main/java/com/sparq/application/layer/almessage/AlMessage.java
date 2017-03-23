package com.sparq.application.layer.almessage;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlMessage {

    private ApplicationLayerPdu.TYPE mMessageType;

    public AlMessage(ApplicationLayerPdu.TYPE messageType){
        mMessageType = messageType;
    }


}
