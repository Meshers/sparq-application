package com.sparq.util;

import java.nio.charset.Charset;

/**
 * Created by sarahcs on 3/20/2017.
 */

public class Constants {

    public final static Charset CHARSET = Charset.forName("UTF-8");

    public static final long MAX_TIME_BETWEEN_SEND = 15000;
    public static final int INITIAL_VOTE_COUNT = 0;
    public static final int MIN_QUESTION_MARKS = 0;
    public static final byte DEFAULT_ADDRESS = (byte) 0;
    public static final byte TEACHER_ADDRESS = (byte) 1;

    public static final String UI_ENABLE_BROADCAST_INTENT = "com.sparq.application.UI_ENABLE_INTENT";
    public static final String UI_DISABLE_BROADCAST_INTENT = "com.sparq.application.UI_DISABLE_INTENT";

}
