package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat;


import java.util.Map;

public class HeartBeatProtocol {
    //Header size
    public static final int MSG_TYPE_SIZE = 1;
    public static final int MSG_LENGTH_SIZE = 3;

    //Body size
    public static final int SENDER_NAME_SIZE = 30;
    public static final int SENDER_CHANNEL_SIZE = 1;
    public static final int QUEUE_NAME_SIZE = 40;
    public static final int SENDER_GROUP_SIZE = 20;
    public static final int EXCHANGE_NAME_SIZE = 20;
    public static final int STATUS_SIZE = 1;
    public static final int EXPIRE_TIME_SIZE= 14;

    // Header
    public static final String HEART = "H";
    public static final String BEAT = "B";
    public static final String SMS = "S";
    public static final String MMS = "M";
    public static final String KKO = "K";
    public static final String RCS = "R";

    public static final int BEAT_LENGTH = 4;

    public static final int HEART_LENGTH = 130;

    //Heart status code
    public static final String HEART_SUCCESS = "0";
    public static final String DST_CONNECTION_ERROR = "1";


    public static final String BEAT_SUCCESS = "0";
    public static final String INVALID_SENDER_NAME = "1";
    public static final String INVALID_SENDER_CHANNEL = "2";
    public static final String INVALID_QUEUE_NAME = "3";
    public static final String INVALID_CONSUME_TAG = "4";
    public static final String INVALID_EXPIRE_TIME = "5";
    public static final String INVALID_REQUEST_BODY_ERROR = "6";
    public static final String SERVER_ERROR = "9";



    // Constants
    public static final int DEFAULT_EXPIRE_TIME = 30;
    public static final int DEFAULT_HEART_TIME = 20;
    public static final int BACKGROUND_RETRY_TIME = 5;
    public static final int MAX_RETRY_COUNT = 3;

    public static final Map<String, String> heartBeatResultMap = Map.of(HeartBeatProtocol.BEAT_SUCCESS, "BEAT_SUCCESS",
            HeartBeatProtocol.INVALID_SENDER_NAME, "INVALID_SENDER_NAME",
            HeartBeatProtocol.INVALID_SENDER_CHANNEL, "INVALID_SENDER_CHANNEL",
            HeartBeatProtocol.INVALID_QUEUE_NAME, "INVALID_QUEUE_NAME",
            HeartBeatProtocol.INVALID_CONSUME_TAG, "INVALID_CONSUME_TAG",
            HeartBeatProtocol.INVALID_EXPIRE_TIME, "INVALID_EXPIRE_TIME",
            HeartBeatProtocol.INVALID_REQUEST_BODY_ERROR, "INVALID_BODY_ERROR",
            HeartBeatProtocol.SERVER_ERROR, "SERVER_ERROR");
}
