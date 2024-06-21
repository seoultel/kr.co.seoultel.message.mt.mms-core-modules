package kr.co.seoultel.message.mt.mms.core_module.modules.client.http;

import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.FileServerException;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.NAckException;
import kr.co.seoultel.message.mt.mms.core_module.common.property.HttpClientProperty;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.PersistenceManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class HttpClientHandler {
    protected final HttpClientProperty property;
    protected final PersistenceManager persistenceManager;

    public HttpClientHandler(HttpClientProperty property, PersistenceManager persistenceManager) {
        this.property = property;
        this.persistenceManager = persistenceManager;
    }

    protected abstract void doSubmit(InboundMessage inboundMessage) throws FileServerException, IOException, NAckException;



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
