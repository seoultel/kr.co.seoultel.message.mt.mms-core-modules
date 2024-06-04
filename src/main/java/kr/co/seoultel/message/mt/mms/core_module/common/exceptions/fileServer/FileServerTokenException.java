package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultFileServerConfig;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;

public class FileServerTokenException extends FileServerException {

    public FileServerTokenException(InboundMessage inboundMessage) {
        super(String.format("[FILE-SERVER TOKEN EXCEPTION] USE WRONG FILE-SERVER TOKEN[%s]", DefaultFileServerConfig.TOKEN));
        this.messageDelivery = inboundMessage.getMessageDelivery();
    }

    public FileServerTokenException(MessageDelivery messageDelivery) {
        super(String.format("[FILE-SERVER TOKEN EXCEPTION] USE WRONG FILE-SERVER TOKEN[%s]", DefaultFileServerConfig.TOKEN));
        this.messageDelivery = messageDelivery;
    }
}
