package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq;


import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MsgReportException extends Exception {

    protected Exception originException;
    protected MrReport mrReport;

    public MsgReportException(Exception e, MrReport mrReport) {
        super(e.getMessage());
        this.originException = e;
        this.mrReport = mrReport;
    }
}
