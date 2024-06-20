package kr.co.seoultel.message.mt.mms.core_module.dto;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.NAckException;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.NAckType;
import kr.co.seoultel.message.mt.mms.core_module.modules.consumer.AbstractConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InboundMessage {

    private final long deliveryTag;
    private final AbstractConsumer consumer;

    @Getter
    private final MessageDelivery messageDelivery;

    public InboundMessage(long deliveryTag, MessageDelivery messageDelivery, AbstractConsumer consumer) {
        this.deliveryTag = deliveryTag;
        this.messageDelivery = messageDelivery;
        this.consumer = consumer;
    }

    public void basicAck() throws NAckException {
        try {
            consumer.getChannel().basicAck(deliveryTag, false);
        } catch (AlreadyClosedException | IOException e) {
            log.error(e.getMessage(), e);
            throw new NAckException(this, e, NAckType.ACK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void basicAck(Channel channel) throws NAckException {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (AlreadyClosedException | IOException e) {
            log.error(e.getMessage(), e);
            throw new NAckException(this, e, NAckType.ACK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void basicNack() throws NAckException {
        try {
            consumer.getChannel().basicNack(deliveryTag, false, true);
        } catch (AlreadyClosedException | IOException e) {
            log.error(e.getMessage(), e);
            throw new NAckException(this, e, NAckType.NACK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void basicNack(Channel channel) throws NAckException {
        try {
            channel.basicNack(deliveryTag, false, true);
        } catch (AlreadyClosedException | IOException e) {
            log.error(e.getMessage(), e);
            throw new NAckException(this, e, NAckType.NACK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "InboundMessage{" +
                "consumer=" + consumer +
                ", messageDelivery=" + messageDelivery +
                '}';
    }
}
