package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq;


import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NAckException extends Exception {

    private final NAckType nAckType;

    private final Exception originException;
    private final InboundMessage inboundMessage;

    public NAckException(InboundMessage inboundMessage, Exception originException, NAckType nAckType) {
        super("[SYSTEM] NAckException is occured, re-send ack|nack to rabbit", originException);
        this.inboundMessage = inboundMessage;
        this.originException = originException;
        this.nAckType = nAckType;
    }

}
