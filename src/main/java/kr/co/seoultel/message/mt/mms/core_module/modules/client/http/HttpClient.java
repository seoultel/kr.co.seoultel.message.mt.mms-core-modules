package kr.co.seoultel.message.mt.mms.core_module.modules.client.http;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.FileServerException;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.NAckException;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.client.Client;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Getter
public class HttpClient {

    protected final HttpClientHandler handler;

    public HttpClient(HttpClientHandler handler) {
        this.handler = handler;
    }

    public void doSubmit(InboundMessage inboundMessage) throws FileServerException, IOException, NAckException {
        handler.doSubmit(inboundMessage);
    }

    @Override
    public String toString() {
        return "HttpClient{" +
                "handler=" + handler +
                '}';
    }
}
