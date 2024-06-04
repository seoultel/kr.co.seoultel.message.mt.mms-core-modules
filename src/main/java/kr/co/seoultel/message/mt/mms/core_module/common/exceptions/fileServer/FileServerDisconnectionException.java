package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;

public class FileServerDisconnectionException extends FileServerException {

    public FileServerDisconnectionException(InboundMessage inboundMessage) {
        super("[DISCONNECTED TO FILE-SERVER] FAILED TO SEND OF MMS MESSAGE[umsMsgId : " + inboundMessage.getMessageDelivery().getUmsMsgId() + "], DIDN'T DOWNLOADED IMAGE FROM FILE SERVER, SEND BASIC-NACK TO RABBIT");
        this.messageDelivery = inboundMessage.getMessageDelivery();
    }


    public FileServerDisconnectionException(MessageDelivery messageDelivery) {
        super("[DISCONNECTED TO FILE-SERVER] FAILED TO SEND OF MMS MESSAGE[umsMsgId : " + messageDelivery.getUmsMsgId() + "], DIDN'T DOWNLOADED IMAGE FROM FILE SERVER, SEND BASIC-NACK TO RABBIT");
        this.messageDelivery = messageDelivery;
    }
}
