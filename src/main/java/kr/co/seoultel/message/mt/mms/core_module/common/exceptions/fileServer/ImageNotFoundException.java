package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;


import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;

import java.util.Collection;
import java.util.stream.Collectors;

public class ImageNotFoundException extends FileServerException {
    public ImageNotFoundException(InboundMessage inboundMessage) {
        super(String.format("[NOT-FOUNDED-IMAGE-IN-REDIS] The message[umsMsgId : %s] has unmatched groupCode with imageId", inboundMessage.getMessageDelivery().getUmsMsgId()));
        this.messageDelivery = inboundMessage.getMessageDelivery();
        this.reportMessage = Constants.IMAGE_NOT_FOUND;
        this.mnoResult = Constants.IMAGE_NOT_FOUND_MNO_RESULT;
    }

    public ImageNotFoundException(InboundMessage inboundMessage, Collection<String> undownloadedImageIdSet) {
        super(String.format("[NOT-FOUNDED-IMAGE-IN-REDIS] The message[umsMsgId : %s]'s images[%s] is not founded in redis", inboundMessage.getMessageDelivery().getUmsMsgId(), undownloadedImageIdSet.stream().collect(Collectors.joining(", "))));
        this.messageDelivery = inboundMessage.getMessageDelivery();
        this.reportMessage = Constants.IMAGE_NOT_FOUND;
        this.mnoResult = Constants.IMAGE_NOT_FOUND_MNO_RESULT;
    }
}
