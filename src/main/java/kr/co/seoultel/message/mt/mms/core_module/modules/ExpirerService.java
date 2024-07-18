package kr.co.seoultel.message.mt.mms.core_module.modules;

import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.ImageNotFoundException;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.redis.RedisService;
import kr.co.seoultel.message.mt.mms.core_module.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ExpirerService {

    private final RedisService redisService;

    public ExpirerService(RedisService redisService) {
        this.redisService = redisService;
    }

    public boolean isExpiredImage(String groupCode, String imageId) {
        String key = RedisUtil.getRedisKeyOfImage(groupCode);
        return Boolean.FALSE.equals(redisService.hasKey(key, imageId));
    }


    public void hasExpiredImages(InboundMessage inboundMessage, String groupCode, Collection<String> imageIds) throws ImageNotFoundException {
        List<String> expiredImageIds = imageIds.stream().filter((imageId) -> isExpiredImage(groupCode, imageId)).collect(Collectors.toList());

        if (!expiredImageIds.isEmpty()) throw new ImageNotFoundException(inboundMessage, expiredImageIds);
    }

}
