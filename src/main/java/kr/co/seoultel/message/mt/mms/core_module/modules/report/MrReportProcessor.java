package kr.co.seoultel.message.mt.mms.core_module.modules.report;


import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultDataVaultConfig;
import kr.co.seoultel.message.mt.mms.core_module.modules.PersistenceManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
public class MrReportProcessor extends Thread {

    protected final MrReportService reportService;
    protected final PersistenceManager persistenceManager;


    /* *******************
     * ** For DataValut **
     * ******************* */
    protected final ConcurrentLinkedQueue<MrReport> reportQueue;

    protected final DataVault<MrReport> reportQueueDataVault;

    public MrReportProcessor(PersistenceManager persistenceManager, MrReportService reportService, ConcurrentLinkedQueue<MrReport> reportQueue) {
        this.persistenceManager = persistenceManager;
        this.reportService = reportService;

        this.reportQueue = reportQueue;
        this.reportQueueDataVault = new DataVault<>("report-data-vault", DefaultDataVaultConfig.REPORT_FILE_PATH);
    }


    @PostConstruct
    public void init() {
        super.setName("report-processor");
        Optional<List<MrReport>> opt = reportQueueDataVault.readAll(MrReport.class);
        if (opt.isPresent()) {
            List<MrReport> mrReports = opt.get();
            reportQueue.addAll(mrReports);
            log.info("[{}] Successfully transfer message[{}] in file[{}] to reportQueue", reportQueueDataVault.getName(), mrReports.size(), DefaultDataVaultConfig.REPORT_FILE_PATH);
        } else {
            log.info("[{}] Message in file[{}] is empty", reportQueueDataVault.getName(), DefaultDataVaultConfig.REPORT_FILE_PATH);
        }
    }



    @PreDestroy
    public void destroy() {
        this.interrupt();
        reportQueueDataVault.destroy(reportQueue);
        log.info("[SHUTDOWN] ReportProcessor is gracefully shutdown");
    }
}
