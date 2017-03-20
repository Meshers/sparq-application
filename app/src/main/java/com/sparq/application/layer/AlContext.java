package com.sparq.application.layer;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

/**
 * Created by sarahcs on 3/19/2017.
 */

public class AlContext {

    //Todo: http://stackoverflow.com/questions/16682847/how-to-manually-include-external-aar-package-using-new-gradle-android-build-syst
    //It says libs folder which I couldn't find, didn't place it cause didn't want to mess up with anything

    public interface Callback {
        void transmitPdu(ApplicationLayerPdu pdu);

       // void sendUpperLayer(LlMessage message);
    }

    private final byte sessionId;


}
