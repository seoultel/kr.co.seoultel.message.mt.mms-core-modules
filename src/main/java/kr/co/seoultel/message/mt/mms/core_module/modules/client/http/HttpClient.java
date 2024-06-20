package kr.co.seoultel.message.mt.mms.core_module.modules.client.http;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.client.Client;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class HttpClient extends Client {

    protected final HttpClientHandler handler;

    public HttpClient(HttpClientHandler handler) {
        this.handler = handler;
    }

    public void doSubmit(InboundMessage inboundMessage) {
        handler.doSubmit(inboundMessage);
    }
}
