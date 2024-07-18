package kr.co.seoultel.message.mt.mms.core_module.modules.report;


import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
public class MrReportProcessor extends Thread {

    protected final MrReportService reportService;

    protected final HashMapStorage<String, MessageDelivery> deliveryStorage;
    protected final QueueStorage<MrReport> reportQueueStorage;


    public MrReportProcessor(MrReportService reportService, HashMapStorage<String, MessageDelivery> deliveryStorage, QueueStorage<MrReport> reportQueueStorage) {
        this.reportService = reportService;

        this.deliveryStorage = deliveryStorage;
        this.reportQueueStorage = reportQueueStorage;
    }


    @PostConstruct
    public void init() {
        super.setName("report-processor");
    }



    @PreDestroy
    public void destroy() {
        this.interrupt();
        log.info("[SHUTDOWN] ReportProcessor is gracefully shutdown");
    }
}
