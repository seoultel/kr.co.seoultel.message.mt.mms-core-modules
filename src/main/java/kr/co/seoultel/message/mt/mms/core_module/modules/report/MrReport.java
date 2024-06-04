package kr.co.seoultel.message.mt.mms.core_module.modules.report;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * @summary
 * MrReport 는 kr.co.seoultel.module.report.ReportProcessor 의 ConcurrentLinkedQueue<MrReport> reportQueue 에 저장되는 객체로
 * 리포트를 전송할 MessageDelivery 객체와 전송할 Report 의 타입을 내부 필드로 가지고 있다.
 */
@Slf4j
@Getter @Setter @ToString
public class MrReport {

    private DeliveryState state;
    private MessageDelivery messageDelivery;

    public MrReport(DeliveryState rType) {
        this.state = rType;
    }

    public MrReport(DeliveryState state, MessageDelivery messageDelivery) {
        this.state = state;
        this.messageDelivery = messageDelivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MrReport that = (MrReport) o;
        return state == that.state && Objects.equals(messageDelivery, that.messageDelivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, messageDelivery);
    }
}
