package kr.co.seoultel.message.mt.mms.core_module.modules;

import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.image.ImageService;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class MMSScheduler {

    private final ExpirerService expirerService;
    private final HashMapStorage<String, MessageHistory> historyStorage;

    public MMSScheduler(ExpirerService expirerService, HashMapStorage<String, MessageHistory> historyStorage) {
        this.expirerService = expirerService;
        this.historyStorage = historyStorage;
    }

    @Scheduled(cron = "0 00/30 * * * *")
    public void processExpiredImages() {
        for (Map.Entry<String, String> entry : ImageService.getImages().entrySet()) {
            String imageKey = entry.getKey();
            String imagePath = entry.getValue();

            String groupCode = ImageUtil.getGroupCodeByImageKey(imageKey);
            String imageId = ImageUtil.getImageIdFromImageKey(imageKey);

            try {
                /*
                 * -> 2가지의 케이스가 존재한다.
                 *  1. 이미지가 만료된 경우, 레디스에 "FileExpireData:GroupCode:ImageId" 키에 맞는 데이터가 존재하지 않는다.
                 *  2. 이미지가 만료되지 않은 경우, 레디스에 "FileExpireData:GroupCode:ImageId" 키에 맞는 데이터가 존재한다.
                 */
                if (expirerService.isExpiredImage(groupCode, imageId)) {
                    // 저장기간 만료 이미지 삭제
                    File expiredImage = new File(imagePath);
                    if (expiredImage.exists()) {
                        expiredImage.delete();
                    }

                    // imagePathMap 에서 이미지 삭제
                    ImageService.getImages().remove(imageKey);
                }
            } catch(Exception e) {
                e.getCause();
                log.error(e.getMessage());
            }
        }
    }

    /*
     * TODO : 만료처리
     */
    @Scheduled(fixedDelay = 30000L)
    public void processExpiredMessages() {
        Collection<MessageHistory> snapshot = historyStorage.snapshot();
        snapshot.stream().filter(MessageHistory::isExpire).forEach((messageHistory) -> {
            String messageId = messageHistory.getMessageId();
            historyStorage.remove(messageId);
            log.info("[EXPIRED] Expired message[{}] successfully removed in historyMessage ", messageId);
        });
    }

}
