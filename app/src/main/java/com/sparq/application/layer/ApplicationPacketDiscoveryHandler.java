package com.sparq.application.layer;

import com.sparq.application.layer.almessage.AlMessage;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;

/**
 * Created by sarahcs on 3/22/2017.
 */

public interface ApplicationPacketDiscoveryHandler {
    void handleDiscovery(ApplicationLayerPdu.TYPE type, AlMessage message);
}
