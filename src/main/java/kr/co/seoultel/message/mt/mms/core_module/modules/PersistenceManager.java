package kr.co.seoultel.message.mt.mms.core_module.modules;


import io.lettuce.core.RedisException;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultDataVaultConfig;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultSenderConfig;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.ImageNotFoundException;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.image.ImageService;
import kr.co.seoultel.message.mt.mms.core_module.modules.redis.RedisConnectionChecker;
import kr.co.seoultel.message.mt.mms.core_module.modules.redis.RedisService;
import kr.co.seoultel.message.mt.mms.core_module.utils.ImageUtil;
import kr.co.seoultel.message.mt.mms.core_module.utils.RedisUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;



/**
 * @author simjeonghun
 * @summary
 * 메세지 전송 과정(SUBMIT, SUBMIT_ACK, REPORT, REPORT_ACK) 중에서 어떠한 상황에서도 메세지가 유실되지 않도록 런타임에 메세지의 영속성(Persistence)를 보장해줌을 목표로 한다.
 * 메세지는 ConcurrentHashMap<String, MessageDelivery> 과 레디스(Redis) 에 함께 저장되는데
 * 만약 레디스에 데이터를 저장할 수 없는 경우에는 ConcurrentHashMap<String, MessageDelivery> 에만 메세지가 저장된다.
 *
 * ConcurrentHashMap<String, MessageDelivery> 에 메세지를 저장해둘 때는 예외가 발생하지 않지만 레디스는 커넥션 문제를 비롯한 여러 상황에서 예외가 발생할 수 있으며,
 * 레디스와의 연결이 끊긴 경우에 계속해서 연결을 시도할 경우, 딜레이가 생기기 때문에 RedisConnectionCheck 객체의 boolean isOpen 변수에 커넥션 여부를 따로 저장하여
 * 레디스에 로직을 실행할지 여부를 결정한다.
 */
@Slf4j
public class PersistenceManager {

    protected final RedisService redisService;


    @Getter
    protected final ConcurrentHashMap<String, MessageHistory> messageHistories = new ConcurrentHashMap<>();

    @Getter
    protected final ConcurrentHashMap<String, MessageDelivery> persistenceMap = new ConcurrentHashMap<>();


    protected final DataVault<MessageDelivery> persistenceDataVault;
    protected final DataVault<MessageHistory> messageHistoryDataVault;


    public PersistenceManager(RedisService redisService) {
        this.redisService = redisService;

        persistenceDataVault = new DataVault<>("persistence-data-vault", DefaultDataVaultConfig.PERSISTENCE_FILE_PATH);
        messageHistoryDataVault = new DataVault<>("message-history-data-vault", DefaultDataVaultConfig.MESSAGE_HISTORIES_FILE_PATH);
    }


    @PostConstruct
    public void postConstruct() {
        Optional<List<MessageDelivery>> optionalMessageDeliveries = persistenceDataVault.readAll(MessageDelivery.class);
        if (optionalMessageDeliveries.isPresent()) {
            List<MessageDelivery> messageDeliveries = optionalMessageDeliveries.get();
            messageDeliveries.forEach((messageDelivery) -> {
                persistenceMap.put(messageDelivery.getUmsMsgId(), messageDelivery);
            });

            log.info("[{}] Successfully transfer message[{}] in file[{}] to persistenceMap", persistenceDataVault.getName(), messageDeliveries.size(), DefaultDataVaultConfig.PERSISTENCE_FILE_PATH);
        } else {
            log.info("[{}] Message in file[{}] is empty", persistenceDataVault.getName(), DefaultDataVaultConfig.PERSISTENCE_FILE_PATH);
        }

        Optional<List<MessageHistory>> optionalMessageHistoryList = messageHistoryDataVault.readAll(MessageHistory.class);
        if (optionalMessageHistoryList.isPresent()) {
            List<MessageHistory> messageHistoryList = optionalMessageHistoryList.get();
            messageHistoryList.forEach((messageHistory -> messageHistories.put(messageHistory.getUmsMsgId(), messageHistory)));

            log.info("[{}] Successfully transfer message[{}] in file[{}] to MessageHistoryMap", persistenceDataVault.getName(), messageHistories.size(), DefaultDataVaultConfig.MESSAGE_HISTORIES_FILE_PATH);
        } else {
            log.info("[{}] messages in file[{}] is empty", persistenceDataVault.getName(), DefaultDataVaultConfig.MESSAGE_HISTORIES_FILE_PATH);
        }
    }


    @Scheduled(cron = "0 00/30 * * * *")
    public void scheduler1() {
        removeExpiredImages();
    }

    @Scheduled(fixedDelay = 30000L)
    public void scheduler2() {
        removeExpiredMessages();
    }

    /*
     * ***************************************
     * ** removeExpiredMessagesSecheduler() **
     * ***************************************
     *
     * 1. PersistenceManager는 메세지 전송 완료 시점에 save() 메서드가 호출되면 자체 맵(submittingMsgs)에
     *    MessageDelivery에서 umsMsgId와 전송 완료 시간을 담아 MsgSubmitTime 객체를 만든다.
     *
     * 2. 해당 MsgSubmitTime 객체는 리포트 수신 후 PersistenceManager 가 삭제될 때 같이 삭제되는데
     *    이를 통해 리포트가 오지 않은 메세지들 중 만료시간(3일)이 지난 메세지들을 삭제하는 역할을 한다.
     */
    // @Scheduled(fixedDelay = 3000L)
    public void removeExpiredMessages() {
        messageHistories.values().stream().filter(MessageHistory::isExpire).forEach((messageHistory) -> {
            String umsMsgId = messageHistory.getUmsMsgId();
            LocalDateTime submitTime = messageHistory.getSubmitTime();

            removeMessageByUmsMsgId(umsMsgId);
            log.info("[SYSTEM] MESSAGE[{}], INSERTED IN SUBMIT-MAP WHEN {}, IS EXPIRED AND REMOVED", umsMsgId, DateUtil.parseLocalDateToString(submitTime));
        });
    }

    public Optional<MessageDelivery> findMessageByUmsMsgId(String umsMsgId) {
        MessageDelivery messageDelivery = persistenceMap.get(umsMsgId);
        if (messageDelivery != null) {
            return Optional.of(messageDelivery);
        }

        if (RedisConnectionChecker.isConnected()) {
            try {
                // umsMsgId 넣어야함
                return redisService.findMessageByUmsMsgIdToRedis(umsMsgId);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return Optional.empty();
    }

    /*
     * SUBMIT, SUBMIT-ACK, REPORT 의 시간이 동일할정도로 빠르게 응답이 오는 경우에 java.util.ConcurrentModificationException 발생 가능성이 있으므로 주의
     */
    public void saveMessageByUmsMsgId(String umsMsgId, MessageDelivery messageDelivery) {
        try {
            persistenceMap.put(umsMsgId, messageDelivery);

            MessageHistory messageHistory = new MessageHistory(umsMsgId, LocalDateTime.now());
            messageHistories.put(umsMsgId, messageHistory);

            // umsMsgId 넣어야함
            if (RedisConnectionChecker.isConnected()) {
                try {
                    redisService.saveMessageByUmsMsgId(umsMsgId, messageDelivery);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[SYSTEM] EXCEPTION THROWED DURING SAVING MESSAGE[{}]",  messageDelivery.getUmsMsgId(), e);
        }
    }


    public boolean updateMessageByUmsMsgId(String umsMsgId, MessageDelivery messageDelivery) {
        persistenceMap.put(umsMsgId, messageDelivery);

        if (RedisConnectionChecker.isConnected()) {
            try {
                return redisService.updateMessageByUmsMsgIdToRedis(umsMsgId, messageDelivery, Duration.ZERO);
            } catch(Exception e) {
                log.error("Exception : {}", e.getMessage());
            }
        }

        return false;
    }

    public boolean isExistsByUmsMsgId(String umsMsgId) {
        if (persistenceMap.containsKey(umsMsgId)) {
            return true;
        }

        if (RedisConnectionChecker.isConnected()) {
            return redisService.isExistsMessageByUmsMsgId(umsMsgId);
        }

        return false;
    }

    public void removeMessageByUmsMsgId(String umsMsgId) {
        persistenceMap.remove(umsMsgId);

        if (RedisConnectionChecker.isConnected()) {
            redisService.removeMessageByUmsMsgId(umsMsgId);
        }

        messageHistories.remove(umsMsgId);
    }




    /*
     * ==================================================================================
     *                                    ABOUT IMAGE
     * ==================================================================================
     */
    public boolean isExpiredImage(String groupCode, String imageId) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        if (RedisConnectionChecker.isConnected()) {
            String imageKey = RedisUtil.getRedisKeyOfImage(groupCode);
            return !redisService.isExistsImage(imageKey, imageId); // hasImage -> not expried
        }

        return false;
    }

    public void findExpiredImagesInRedis(InboundMessage inboundMessage, String groupCode, Collection<String> undownloadedImageIdSet) throws ImageNotFoundException {
        List<String> expiredImageIds = undownloadedImageIdSet.stream().filter((imageId) -> {
                    String imageKey = RedisUtil.getRedisKeyOfImage(groupCode);
                    return !redisService.isExistsImage(imageKey, imageId);
                })
                .collect(Collectors.toList());

        if (expiredImageIds.isEmpty()) throw new ImageNotFoundException(inboundMessage, expiredImageIds);
    }


    public void removeExpiredImages() {
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
                if (isExpiredImage(groupCode, imageId)) {
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



    @PreDestroy
    public void destroy() {
        List<MessageDelivery> backupDatas = new ArrayList<>();

        Set<Map.Entry<String, MessageDelivery>> entries = persistenceMap.entrySet();
        log.info("[SHUTDOWN, SENDER[{}]] SIZE OF MESSAGES IN HASH-MAP : {}", DefaultSenderConfig.NAME, entries.size());

        if (!entries.isEmpty()) {
            entries.forEach((Map.Entry<String, MessageDelivery> entry) -> {
                String umsMsgId = entry.getKey();
                MessageDelivery messageInHashMap = entry.getValue();

                if (RedisConnectionChecker.isConnected()) {
                    // 레디스에 메세지가 존재하지 않는 경우
                    if (!redisService.isExistsMessageByUmsMsgId(umsMsgId)) {
                        redisService.saveMessageByUmsMsgId(umsMsgId, messageInHashMap);
                        log.info("[SHUTDOWN] MESSAGE[{}] IS SUCCESSFULLY TRANSFERED TO REDIS : [{}]", umsMsgId, messageInHashMap);
                    }
                    // 레디스에 메세지가 존재하는 경우
                    else {
                        backupDatas.add(messageInHashMap);
                        log.info("[SHUTDOWN] \"MESSAGE[{}] IS LOADED TO DATA-VAULT\"", umsMsgId);
                    }
                } else {
                    backupDatas.add(messageInHashMap);
                    log.info("[SHUTDOWN] \"MESSAGE[{}] IS LOADED TO DATA-VAULTS\"", umsMsgId);
                }
            });
        }

        persistenceDataVault.destroy(backupDatas);
        messageHistoryDataVault.destroy(messageHistories.values());
        log.info("[SHUTDOWN] SENDER[{}] IS GRACEFULLY SHUTDOWN", DefaultSenderConfig.NAME);
    }
}
