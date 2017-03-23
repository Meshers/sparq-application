package com.sparq.application.layer;

import com.sparq.application.layer.almessage.AlMessage;

/**
 * Created by sarahcs on 3/22/2017.
 */

public interface ApplicationPacketDiscoveryHandler {
    void handleDiscovery(AlMessage message);
}
