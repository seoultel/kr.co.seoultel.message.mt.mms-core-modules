package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;


import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryType;
import kr.co.seoultel.message.mt.mms.core.util.FallbackUtil;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;

public class AttachedImageFormatException extends FileServerException {

    public AttachedImageFormatException(InboundMessage inboundMessage) {
        super("The message[umsMsgId : " + inboundMessage.getMessageDelivery().getUmsMsgId() + "] excess number of images");
        this.messageDelivery = inboundMessage.getMessageDelivery();
        this.reportMessage = Constants.IMAGE_CNT_OVER;
        this.mnoResult = Constants.IMAGE_CNT_OVER_MNO_RESULT;
        this.deliveryType = FallbackUtil.isFallback(messageDelivery) ? DeliveryType.FALLBACK_SUBMIT : DeliveryType.SUBMIT;
    }
}
