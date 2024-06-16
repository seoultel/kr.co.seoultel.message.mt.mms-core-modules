package kr.co.seoultel.message.mt.mms.core_module.utils;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.core.dto.ProcessRecord;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryState;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryType;
import kr.co.seoultel.message.mt.mms.core.messages.Message;
import kr.co.seoultel.message.mt.mms.core.util.FallbackUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultSenderConfig;

import java.util.Map;

public abstract class MMSReportUtil<T extends Message> {

    protected void addProcessRecord(MessageDelivery message, DeliveryType deliveryType) {
        ProcessRecord processRecord = ProcessRecord.builder()
                .processedTime(System.currentTimeMillis())
                .processorName(DefaultSenderConfig.NAME)
                .action(deliveryType.getTypeEng() + "(" + message.getUmsMsgId() + ")")
                .build();

        message.addProcessRecord(processRecord);
    }

    /**
     * Prepare to messageDelivery at submit.
     *
     * @param isFallback      Whether fallback is not
     * @param messageDelivery the message delivery
     */
    public void prepareToSubmit(boolean isFallback, MessageDelivery messageDelivery) {
        messageDelivery.setDeliveryProcess(DefaultSenderConfig.NAME); // 해당 프로세스 고유의 ProcessName 할당

        /* SET DeliveryType(TYPE_FALLBACK_SUBMIT | TYPE_SUBMIT) */
        DeliveryState deliveryState = isFallback ? DeliveryState.FALLBACK_SUBMIT : DeliveryState.SUBMIT;
        DeliveryType deliveryType = isFallback ? DeliveryType.FALLBACK_SUBMIT : DeliveryType.SUBMIT;

        messageDelivery.setDeliveryState(deliveryState.getState());
        messageDelivery.setDeliveryType(deliveryType.getType());

        addProcessRecord(messageDelivery, deliveryType);
    }


    /**
     * Prepare to messageDelivery at submit-ack to send report to mcmp reporter.
     *
     * @param message     the lghv message received from LGHV when submit-ack
     * @param messageDelivery the message delivery in redis or hash-map
     */
    protected abstract void prepareToSubmitAck(MessageDelivery messageDelivery, T message);

    /**
     * Gets result of messageDelivery for send report to mcmp reporter when received submit-ack from LGHV
     *
     * @param messageDelivery the message delivery
     * @param message     the message received from Destination when submit-ack
     * @return * @return the assigned mandatory values to send submit-ack when to send report to mcmp reporter
     */
    protected abstract Map<String, Object> getSubmitAckResult(MessageDelivery messageDelivery, T message);

    /**
     * Sets delivery type and state at submit ack.
     *
     * @param messageDelivery the message delivery
     * @param isSuccess     the is successed
     */
    public void setDeliveryTypeAndStateAtSubmitAck(boolean isSuccess, MessageDelivery messageDelivery) {
        DeliveryState deliveryState;
        DeliveryType deliveryType;
        if (FallbackUtil.isFallback(messageDelivery)) {
            deliveryState = isSuccess ? DeliveryState.FALLBACK_SUBMIT : DeliveryState.FALLBACK_FAILED;
            deliveryType = DeliveryType.FALLBACK_SUBMIT_ACK;
        } else {
            deliveryState = isSuccess ? DeliveryState.SUBMIT : DeliveryState.FAILED;
            deliveryType = DeliveryType.SUBMIT_ACK;
        }

        messageDelivery.setDeliveryState(deliveryState.getState());
        messageDelivery.setDeliveryType(deliveryType.getType());

        addProcessRecord(messageDelivery, deliveryType);
    }


    /**
     * Prepare to report.
     *
     * @param messageDelivery the message delivery
     * @param message     the message when received report
     */
    protected abstract void prepareToReport(MessageDelivery messageDelivery, T message);

    /**
     * Gets report result.
     *
     * @param isSuccessToSend the is success to send
     * @param messageDelivery the message delivery
     * @param message     the lghv-message received from LGHV when report
     * @return * @return the assigned mandatory values to send report when to send report to mcmp reporter
     */
    protected abstract Map<String, Object> getReportResult(boolean isSuccessToSend, MessageDelivery messageDelivery, T message);

    /**
     * Sets delivery type and state at report.
     *
     * @param isSuccess     the is successed
     * @param messageDelivery the message delivery
     */
    public void setDeliveryTypeAndStateAtReport(boolean isSuccess, MessageDelivery messageDelivery) {
        DeliveryState deliveryState;
        DeliveryType deliveryType;
        if (FallbackUtil.isFallback(messageDelivery)) {
            deliveryState = isSuccess ? DeliveryState.FALLBACK_SUCCESS : DeliveryState.FALLBACK_FAILED;
            deliveryType = DeliveryType.FALLBACK_REPORT;
        } else {
            deliveryState = isSuccess ? DeliveryState.SUCCESS : DeliveryState.FAILED;
            deliveryType = DeliveryType.REPORT;
        }

        messageDelivery.setDeliveryState(deliveryState.getState());
        messageDelivery.setDeliveryType(deliveryType.getType());

        addProcessRecord(messageDelivery, deliveryType);
    }
}
