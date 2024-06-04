package kr.co.seoultel.message.mt.mms.core_module.utils;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.core.dto.ProcessRecord;
import kr.co.seoultel.message.mt.mms.core.messages.Message;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultSenderConfig;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.DeliveryState;

public abstract class MessageUtil {

    public static void addProcessRecord(MessageDelivery message, DeliveryState deliveryState) {
        ProcessRecord processRecord = ProcessRecord.builder()
                .processedTime(System.currentTimeMillis())
                .processorName(DefaultSenderConfig.NAME)
                .action(deliveryState + "(" + message.getUmsMsgId() + ")")
                .build();

        message.addProcessRecord(processRecord);
    }

    public void prepareToSubmit(MessageDelivery messageDelivery) {
        messageDelivery.setDeliveryType(MessageDelivery.TYPE_SUBMIT);   // SUBMIT �ܰ�
        addProcessRecord(messageDelivery, DeliveryState.SUBMIT);
        messageDelivery.setDeliveryProcess(DefaultSenderConfig.NAME); // �ش� ���μ��� ������ ProcessName �Ҵ�
    }

    abstract void prepareToSubmitAckForReport(Message message, MessageDelivery messageDelivery);

    abstract void setResultAtSubmitAck(Message message, MessageDelivery messageDelivery);

    public static void setDeliveryTypeAndStateAtSubmitAck(MessageDelivery messageDelivery, boolean isSuccessed) {
        int deliveryState = isSuccessed ? MessageDelivery.STATE_SUBMIT : MessageDelivery.STATE_FAILED;
        messageDelivery.setDeliveryState(deliveryState);
        messageDelivery.setDeliveryType(MessageDelivery.TYPE_SUBMIT_ACK);
    }
}
