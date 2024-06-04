package kr.co.seoultel.message.mt.mms.core_module.common.aspect;

import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.MsgReportException;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class RabbitMqAspect {
    protected final ConcurrentLinkedQueue<MrReport> reportQueue;

    public RabbitMqAspect(ConcurrentLinkedQueue<MrReport> reportQueue) {
        this.reportQueue = reportQueue;
    }

    /**
     *
     * Handle exception during send report to rabbit.
     *
     * MsgFailException, throwing ReportService.sendRpoert(MessageDelivery obj) method, has MessageDelivery And throwing rabbitTemplate.convertAndSend(Object obj)
     * MessageDelivery instance in MsgFailException requeued
     *
     */
    public void handleExceptionDuringSendReportToRabbit(MsgReportException ex) {
        CommonUtil.doThreadSleep(500L);
        reportQueue.add(ex.getMrReport());
        log.info("[SYSTEM] successfully message[umsMsgId : {}] to be reququed to reportQueue", ex.getMrReport().getMessageDelivery().getUmsMsgId());

        if (ex.getOriginException() instanceof java.net.ConnectException) {
            log.error("???");
        }
    }
}
