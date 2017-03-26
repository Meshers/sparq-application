package com.sparq.application.layer.pdu;

import android.util.Log;

import java.nio.charset.Charset;

/**
 * Created by sarahcs on 3/19/2017.
 */

public abstract class ApplicationLayerPdu {

    public final static Charset CHARSET = Charset.forName("UTF-8");

    public enum TYPE {
        QUESTION(3),
        ANSWER(4),
        QUESTION_VOTE(5),
        ANSWER_VOTE(6);

        private int type;

        TYPE(int numVal) {
            this.type = numVal;
        }

        public int getType() {
            return type;
        }

    }

    public final static int TOT_SIZE = 200;
    public final static int TYPE_BYTES = 1;

    private final TYPE mType;

    public ApplicationLayerPdu(TYPE type){
        this.mType = type;
    }

    public TYPE getType() {
        return mType;
    }

    public static byte getTypeEncoded(TYPE type) {
        return (byte) (type.ordinal() + 1);
    }

    public static TYPE getTypeDecoded(byte type) {
        try{
            return TYPE.values()[type - 1];
        }
        catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    public abstract byte[] encode();


}
