package com.sparq.quizpolls.application.layer.pdu;

import java.nio.charset.Charset;

/**
 * Created by sarahcs on 3/19/2017.
 */

public abstract class ApplicationLayerPdu {

    public final static Charset CHARSET = Charset.forName("UTF-8");

    public enum TYPE {
        QUIZ_QUESTION(1),
        QUIZ_ANSWER(2),
        POLL_QUESTION(3),
        POLL_ANSWER(4),
        QUESTION(5),
        ANSWER(6),
        QUESTION_VOTE(7),
        ANSWER_VOTE(8);

        private int type;

        TYPE(int numVal) {
            this.type = numVal;
        }

        public int getType() {
            return type;
        }

    }

    public static final int TOT_SIZE_BT = 200;
    public static final int TOT_SIZE_WIFI = 10;
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
