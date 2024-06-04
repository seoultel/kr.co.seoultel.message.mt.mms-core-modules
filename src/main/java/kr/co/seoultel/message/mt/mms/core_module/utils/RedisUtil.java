package kr.co.seoultel.message.mt.mms.core_module.utils;

import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultSenderConfig;

import static kr.co.seoultel.message.mt.mms.core.constant.Constants.MMS_PREFIX;
import static kr.co.seoultel.message.mt.mms.core.constant.Constants.REDIS_IMAGE_PREFIX;

public class RedisUtil {
    public static String getRedisKeyOfMessage() {
        return String.join(":", MMS_PREFIX, DefaultSenderConfig.NAME);
    }

    public static String getRedisKeyOfImage(String groupCode) {
        return String.join(":", REDIS_IMAGE_PREFIX, groupCode);
    }
}
