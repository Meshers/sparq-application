//package com.sparq.application.layer.pdu;
//
///**
// * Created by sarahcs on 3/30/2017.
// */
//
//public class PollPdu extends ApplicationLayerPdu {
//
//    private final static int POLL_ID_BYTES = 1;
//    private final static int QUESTION_ID_BYTES = 1
//    private final static int THREAD_CREATOR_ID_BYTES = 1;
//    private final static int ANSWER_CREATOR_ID_BYTES = 1;
//
//    private final static int PDU_QUESTION_HEADER_BYTES = TYPE_BYTES + POLL_ID_BYTES + THREAD_CREATOR_ID_BYTES + QUESTION_ID_BYTES;
//    private final static int PAYLOAD_QUESTION_MAX_BYTES = TOT_SIZE - PDU_QUESTION_HEADER_BYTES;
//
//    private final static int PDU_ANSWER_HEADER_BYTES = TYPE_BYTES + POLL_ID_BYTES + THREAD_CREATOR_ID_BYTES + QUESTION_ID_BYTES + ANSWER_CREATOR_ID_BYTES;
//    private final static int PAYLOAD_ANSWER_MAX_BYTES = TOT_SIZE - PDU_ANSWER_HEADER_BYTES;
//
//    private final static int PDU_QUESTION_VOTE_HEADER_BYTES = TYPE_BYTES + THREAD_CREATOR_ID_BYTES + THREAD_ID_BYTES;
//    private final static int PAYLOAD_QUESTION_VOTE_MAX_BYTES = TOT_SIZE - PDU_QUESTION_VOTE_HEADER_BYTES;
//
//    private final static int PDU_ANSWER_VOTE_HEADER_BYTES = TYPE_BYTES + THREAD_CREATOR_ID_BYTES + THREAD_ID_BYTES + SUB_THREAD_CREATOR_ID_BYTES + SUB_THREAD_ID_BYTES;
//    private final static int PAYLOAD_ANSWER_VOTE_MAX_BYTES = TOT_SIZE - PDU_ANSWER_VOTE_HEADER_BYTES;
//
//    private final static int PAYLOAD_MAX_BYTES = Math.max(
//            PAYLOAD_QUESTION_MAX_BYTES,
//            Math.max(PAYLOAD_ANSWER_MAX_BYTES,
//                    Math.max(PAYLOAD_QUESTION_VOTE_MAX_BYTES,
//                            PAYLOAD_ANSWER_VOTE_MAX_BYTES))
//    );
//
//    public final static int HEADER_MAX_BYTES = Math.max(
//            PDU_QUESTION_HEADER_BYTES,
//            Math.max(PDU_ANSWER_HEADER_BYTES,
//                    Math.max(PDU_QUESTION_VOTE_HEADER_BYTES,
//                            PDU_ANSWER_VOTE_HEADER_BYTES))
//    );
//}
