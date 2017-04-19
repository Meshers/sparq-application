package com.sparq.quizpolls.application.layer;

import com.sparq.quizpolls.application.layer.almessage.AlMessage;
import com.sparq.quizpolls.application.layer.pdu.ApplicationLayerPdu;

/**
 * Created by sarahcs on 3/22/2017.
 */

public interface ApplicationPacketDiscoveryHandler {
    void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage message);
}
