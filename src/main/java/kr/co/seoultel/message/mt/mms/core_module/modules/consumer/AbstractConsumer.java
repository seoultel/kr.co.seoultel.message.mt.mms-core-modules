package kr.co.seoultel.message.mt.mms.core_module.modules.consumer;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultDataVaultConfig;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
@Getter
public abstract class AbstractConsumer implements Consumer {

    @Value("${mt.exchange}")
    protected String mtExchangeName;
    @Value("${mt.queue}")
    protected String mtQueueName;

    @Value("${mr.exchange}")
    protected String mrExchangeName;
    @Value("${mr.queue}")
    protected String mrQueueName;



    protected Channel channel;
    protected String consumerTag;


    protected final ConcurrentLinkedQueue<MrReport> reportQueue;

    protected DataVault<MessageDelivery> republishQueueDataVault;
    protected final ConcurrentLinkedQueue<MessageDelivery> republishQueue;


    public AbstractConsumer(ConcurrentLinkedQueue<MrReport> reportQueue, ConcurrentLinkedQueue<MessageDelivery> republishQueue) {
        this.reportQueue = reportQueue;
        this.republishQueue = republishQueue;

        republishQueueDataVault = new DataVault<>("republish-data-vault", DefaultDataVaultConfig.REPUBLISH_FILE_PATH);
    }

    @PostConstruct
    public void postConstruct() {
        Optional<List<MessageDelivery>> optionalRepublishMessages = republishQueueDataVault.readAll(MessageDelivery.class);
        if (optionalRepublishMessages.isPresent()) {
            List<MessageDelivery> republishMessages = optionalRepublishMessages.get();
            republishQueue.addAll(republishMessages);

            log.info("[{}] Successfully transfer message[{}] in file[{}] to republishQueue", republishQueueDataVault.getName(), republishMessages.size(), DefaultDataVaultConfig.REPUBLISH_FILE_PATH);
        } else {
            log.info("[{}] Message in file[{}] is empty", republishQueueDataVault.getName(), DefaultDataVaultConfig.REPUBLISH_FILE_PATH);
        }
    }

    @Scheduled(fixedDelay = 10000L)
    public void republishMessageInMtQueue() {
        while (!republishQueue.isEmpty()) {
            if (channel != null && channel.isOpen()) {
                MessageDelivery messageDelivery = republishQueue.remove();

                try {
                    basicPublish(messageDelivery);
                } catch (Exception e) {
                    republishQueue.add(messageDelivery);
                    log.error("[FAILED RE-PUBLISH TO MT-QUEUE] FAILED RE-PUBLISHED MESSAGE[{}] TO MT QUEUE, RETRY AFTER 30SEC", messageDelivery.getUmsMsgId(), e);
                }
            }
        }
    }

    @Override
    public void handleConsumeOk(String s) {
        log.info("[CONSUMER] Consumer[tag : {}] successfully ready to consume of message.", consumerTag);
    }

    @Override
    public void handleCancelOk(String s) {
        log.info("[CONSUMER] Consumer[tag : {}] successfully cancelled.", consumerTag);
    }

    @Override
    public void handleRecoverOk(String s) {
        log.info("[CONSUMER] Consumer[tag : {}] successfully recovered.", consumerTag);
    }

    public void doConsume() {
        if (channel != null && channel.isOpen()) {
            try {
                this.consumerTag = channel.basicConsume(mtQueueName, false, this);
            } catch (IOException | AlreadyClosedException e) {
                log.error("[CONSUME] Fail to consume message in consumer ! ", e);
            }
        }
    }

    public void basicPublish(MessageDelivery messageDelivery) throws IOException {
        String toJson = ConvertorUtil.convertObjectToJson(messageDelivery);
        channel.basicPublish(mtExchangeName, mtQueueName, false, null, toJson.getBytes());
        log.info("[RE-PUBLISH TO MT-QUEUE] SUCCESSFULLY RE-PUBLISHED MESSAGE[{}] TO MT QUEUE", messageDelivery.getUmsMsgId());
    }

    protected abstract void createChannel();

    protected abstract void closeChannel();
}
