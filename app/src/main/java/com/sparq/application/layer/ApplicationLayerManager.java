package com.sparq.application.layer;

import com.sparq.application.layer.pdu.ApplicationLayerPdu;

import java.io.UnsupportedEncodingException;

import test.com.blootoothtester.network.linklayer.LinkLayerManager;
import test.com.blootoothtester.network.linklayer.LlMessage;
import test.com.blootoothtester.util.Logger;

/**
 * This class contains information about the current context in which the LinkLayer is running such
 * as messages received so far, session ID, etc.
 */

public class ApplicationLayerManager {
    private AlContext mAlContext;
    private LinkLayerManager mLinkLayerManager;
    private Logger mLogger = new Logger();

    public ApplicationLayerManager(LinkLayerManager linkLayerManager){
        this.mAlContext = new AlContext();
        mLinkLayerManager = linkLayerManager;

        //I'm sorry
        AlContext.Callback callback = new AlContext.Callback() {
            @Override
            public void transmitPdu(ApplicationLayerPdu pdu) {
                //mLogger.d("ApplicationLayerManager", "sendData: seq.id: " + pdu.getSequenceId());
                //mLinkLayerManager.sendData();
            }

            @Override
            public void sendUpperLayer(AlMessage message) {
                //mLinkLayerManager.send(message);
            }
        }
    }

    public void sendData(byte[] packet, byte type) {
        mAlContext.sendPdu(type, packet);
    }

    public void sendData(String msg, byte toAddr) {
        try {
            sendData(msg.getBytes("UTF-8"), toAddr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
