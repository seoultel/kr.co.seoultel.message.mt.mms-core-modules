package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.util.FallbackUtil;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import lombok.Getter;

import java.util.Collection;
import java.util.List;


public class ImageExpiredException extends FileServerException {

    @Getter
    private final Collection<String> expiredImageIds;

    public ImageExpiredException(InboundMessage inboundMessage, Collection<String> expiredImageIds) {
        super(String.format("[IMAGE_EXPIRED] Message[%s] CONTAINS EXPIRED IMAGES[%s]", inboundMessage.getMessageDelivery().getUmsMsgId(), expiredImageIds));
        this.messageDelivery = inboundMessage.getMessageDelivery();
        this.reportMessage = Constants.IMAGE_IS_EXPIRED;
        this.mnoResult = Constants.IMAGE_IS_EXPIRED_MNO_RESULT;
        this.expiredImageIds = expiredImageIds;
        this.deliveryType = FallbackUtil.isFallback(messageDelivery) ? MessageDelivery.TYPE_FALLBACK_SUBMIT : MessageDelivery.TYPE_SUBMIT;
    }

    public ImageExpiredException(String umsMsgId, List<String> expiredImageIds) {
        super(String.format("[IMAGE_EXPIRED] Message[%s] contains expired images[%s]", umsMsgId, expiredImageIds));
        this.expiredImageIds = expiredImageIds;
    }


}
