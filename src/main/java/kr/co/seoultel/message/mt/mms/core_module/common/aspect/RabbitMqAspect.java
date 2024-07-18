package kr.co.seoultel.message.mt.mms.core_module.common.aspect;

import com.google.gson.reflect.TypeToken;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.MsgReportException;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Aspect
public class RabbitMqAspect {

    protected final QueueStorage<MrReport> reportQueueStorage;

    public RabbitMqAspect(QueueStorage<MrReport> reportQueueStorage) {
        this.reportQueueStorage = reportQueueStorage;
    }

    /**
     *
     * Handle exception during send report to rabbit.
     *
     * MsgFailException, throwing ReportService.sendRpoert(MessageDelivery obj) method, has MessageDelivery And throwing rabbitTemplate.convertAndSend(Object obj)
     * MessageDelivery instance in MsgFailException requeued
     *
     */
    @AfterThrowing(pointcut = "execution(* kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReportService.sendReport(..))", throwing = "ex")
    public void handleExceptionDuringSendReportToRabbit(MsgReportException ex) {
        CommonUtil.doThreadSleep(500L);
        reportQueueStorage.add(ex.getMrReport());
        log.info("[SYSTEM] successfully message[umsMsgId : {}] to be reququed to reportQueue", ex.getMrReport().getMessageDelivery().getUmsMsgId());

        if (ex.getOriginException() instanceof java.net.ConnectException) {
            log.error("???");
        }
    }
}
