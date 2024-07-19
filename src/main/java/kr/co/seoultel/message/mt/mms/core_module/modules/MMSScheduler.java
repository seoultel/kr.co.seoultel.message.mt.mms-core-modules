package kr.co.seoultel.message.mt.mms.core_module.modules;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryState;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryType;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core.util.FallbackUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.image.ImageService;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
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

    protected final ExpirerService expirerService;

    protected final QueueStorage<MrReport> reportQueueStorage;


    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final HashMapStorage<String, MessageDelivery> deliveryStorage;



    public MMSScheduler(ExpirerService expirerService, QueueStorage<MrReport> reportQueueStorage, HashMapStorage<String, MessageHistory> historyStorage, HashMapStorage<String, MessageDelivery> deliveryStorage) {
        this.expirerService = expirerService;
        this.reportQueueStorage = reportQueueStorage;
        this.historyStorage = historyStorage;
        this.deliveryStorage = deliveryStorage;
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
}
