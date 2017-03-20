package com.sparq.application.layer;

import java.io.UnsupportedEncodingException;

/**
 * This class contains information about the current context in which the LinkLayer is running such
 * as messages received so far, session ID, etc.
 */

public class ApplicationLayerManager {
    private AlContext mAlContext;

    public ApplicationLayerManager(){
        this.mAlContext = new AlContext();
    }

    public void sendData(byte[] packet, byte toAddr) {
        mAlContext.sendPdu(toAddr, packet);
    }

    public void sendData(String msg, byte toAddr) {
        try {
            sendData(msg.getBytes("UTF-8"), toAddr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
