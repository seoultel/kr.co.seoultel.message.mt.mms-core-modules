package kr.co.seoultel.message.mt.mms.core_module.modules.report;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.entity.DeliveryType;
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

    private DeliveryType type;
    private MessageDelivery messageDelivery;

    public MrReport(DeliveryType rType) {
        this.type = rType;
    }

    public MrReport(DeliveryType type, MessageDelivery messageDelivery) {
        this.type = type;
        this.messageDelivery = messageDelivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MrReport that = (MrReport) o;
        return type == that.type && Objects.equals(messageDelivery, that.messageDelivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, messageDelivery);
    }
}
