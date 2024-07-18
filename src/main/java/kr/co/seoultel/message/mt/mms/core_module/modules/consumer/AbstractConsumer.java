package kr.co.seoultel.message.mt.mms.core_module.modules.consumer;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultDataVaultConfig;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultRabbitMQConfig;
import kr.co.seoultel.message.mt.mms.core_module.common.property.RabbitMqProperty;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
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

    protected Channel channel;
    protected String consumerTag;

    protected DefaultRabbitMQConfig rabbitMqConfig;
    protected final QueueStorage<MessageDelivery> republishQueueStorage;
    protected final QueueStorage<MrReport> reportQueueStorage;

    public AbstractConsumer(DefaultRabbitMQConfig rabbitMqConfig, QueueStorage<MessageDelivery> republishQueueStorage, QueueStorage<MrReport> reportQueueStorage) {
        this.rabbitMqConfig = rabbitMqConfig;
        this.republishQueueStorage = republishQueueStorage;
        this.reportQueueStorage = reportQueueStorage;
    }

    @Scheduled(fixedDelay = 10000L)
    public void republishMessageInMtQueue() {
        while (!republishQueueStorage.isEmpty()) {
            if (channel != null && channel.isOpen()) {
                MessageDelivery messageDelivery = republishQueueStorage.remove();

                try {
                    basicPublish(messageDelivery);
                } catch (Exception e) {
                    republishQueueStorage.add(messageDelivery);
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
                this.consumerTag = channel.basicConsume(rabbitMqConfig.getMtQueue(), false, this);
            } catch (IOException | AlreadyClosedException e) {
                log.error("[CONSUME] Fail to consume message in consumer ! ", e);
            }
        }
    }

    public void basicPublish(MessageDelivery messageDelivery) throws IOException {
        String toJson = ConvertorUtil.convertObjectToJson(messageDelivery);
        channel.basicPublish(rabbitMqConfig.getMtExchange(), rabbitMqConfig.getMtQueue(), false, null, toJson.getBytes());
        log.info("[RE-PUBLISH TO MT-QUEUE] SUCCESSFULLY RE-PUBLISHED MESSAGE[{}] TO MT QUEUE", messageDelivery.getUmsMsgId());
    }

    protected abstract void createChannel();

    protected abstract void closeChannel();
}
