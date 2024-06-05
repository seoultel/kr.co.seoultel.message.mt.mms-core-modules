package kr.co.seoultel.message.mt.mms.core_module.utils;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.core.dto.ProcessRecord;
import kr.co.seoultel.message.mt.mms.core.messages.Message;
import kr.co.seoultel.message.mt.mms.core.util.FallbackUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultSenderConfig;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.DeliveryState;

import java.util.Map;

public abstract class MMSReportUtil<T extends Message> {

    public void addProcessRecord(MessageDelivery message, DeliveryState deliveryState) {
        ProcessRecord processRecord = ProcessRecord.builder()
                .processedTime(System.currentTimeMillis())
                .processorName(DefaultSenderConfig.NAME)
                .action(deliveryState + "(" + message.getUmsMsgId() + ")")
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
        int deliveryType = isFallback ? MessageDelivery.TYPE_FALLBACK_SUBMIT : MessageDelivery.TYPE_SUBMIT;
        messageDelivery.setDeliveryType(deliveryType);

        addProcessRecord(messageDelivery, DeliveryState.SUBMIT);
    }


    /**
     * Prepare to messageDelivery at submit-ack to send report to mcmp reporter.
     *
     * @param message     the lghv message received from LGHV when submit-ack
     * @param messageDelivery the message delivery in redis or hash-map
     */
    protected abstract void prepareToSubmitAckForReport(MessageDelivery messageDelivery, T message);

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
    public void setDeliveryTypeAndStateAtSubmitAck(MessageDelivery messageDelivery, boolean isSuccess) {
        int deliveryState;
        if (FallbackUtil.isFallback(messageDelivery)) {
            deliveryState = isSuccess ? MessageDelivery.STATE_FALLBACK_SUBMIT : MessageDelivery.STATE_FALLBACK_FAILED;
        } else {
            deliveryState = isSuccess ? MessageDelivery.STATE_SUBMIT : MessageDelivery.STATE_FAILED;
        }

        messageDelivery.setDeliveryState(deliveryState);
        messageDelivery.setDeliveryType(MessageDelivery.TYPE_SUBMIT_ACK);
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
        int deliveryState;
        if (FallbackUtil.isFallback(messageDelivery)) {
            deliveryState = isSuccess ? MessageDelivery.STATE_FALLBACK_SUCCESS : MessageDelivery.STATE_FALLBACK_FAILED;
        } else {
            deliveryState = isSuccess ? MessageDelivery.STATE_SUCCESS : MessageDelivery.STATE_FAILED;
        }

        messageDelivery.setDeliveryState(deliveryState);
        messageDelivery.setDeliveryType(MessageDelivery.TYPE_REPORT);
    }
}
