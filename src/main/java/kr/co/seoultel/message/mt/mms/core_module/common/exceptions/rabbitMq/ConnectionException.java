package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq;

import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import lombok.Getter;

public class ConnectionException extends Exception {

    @Getter
    private final InboundMessage inboundMessage;
    private final Exception ex;
    public ConnectionException(Exception ex, InboundMessage inboundMessage) {
        super(String.format("[Exception in Ack | Nack] {}", ex.getMessage()));
        this.ex = ex;
        this.inboundMessage = inboundMessage;
    }
}
