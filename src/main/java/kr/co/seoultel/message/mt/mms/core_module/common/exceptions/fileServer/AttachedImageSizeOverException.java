package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;


import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryType;
import kr.co.seoultel.message.mt.mms.core.util.FallbackUtil;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;

public class AttachedImageSizeOverException extends FileServerException {
    public AttachedImageSizeOverException(InboundMessage inboundMessage) {
        super("The message[umsMsgId : " + inboundMessage.getMessageDelivery().getUmsMsgId() + "] excess max image's size");
        this.messageDelivery = inboundMessage.getMessageDelivery();
        this.reportMessage = Constants.IMAGE_SIZE_OVER;
        this.mnoResult = Constants.IMAGE_SIZE_OVER_MNO_RESULT;
        this.deliveryType = FallbackUtil.isFallback(messageDelivery) ? DeliveryType.FALLBACK_SUBMIT_ACK : DeliveryType.SUBMIT_ACK;
    }
}
