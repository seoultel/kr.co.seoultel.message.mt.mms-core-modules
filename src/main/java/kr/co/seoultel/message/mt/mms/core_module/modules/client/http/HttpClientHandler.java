package kr.co.seoultel.message.mt.mms.core_module.modules.client.http;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class HttpClientHandler {
    protected final HttpClientProperty property;

    public HttpClientHandler(String cpid, HttpClientProperty property) {
        this.property = property.setCpid(cpid);
    }

    protected abstract void doSubmit(InboundMessage inboundMessage);

    public String getMtExchangeName() {
        return property.getMtExchangeName();
    }

    public String getMtQueueName() {
        return property.getMtQueueName();
    }

    public String getMrExchangeName() {
        return property.getMrExchangeName();
    }

    public String getMrQueueName() {
        return property.getMrQueueName();
    }

    public String getName() {
        return property.getName();
    }

    public String getGroup() {
        return property.getGroup();
    }

    public String getTelecom() {
        return property.getTelecom();
    }

    public String getBpid() {
        return property.getBpid();
    }

    public String getVasId() {
        return property.getVasId();
    }

    public String getVaspId() {
        return property.getVaspId();
    }

}
