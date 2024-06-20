package kr.co.seoultel.message.mt.mms.core_module.modules.client.tcp;

import kr.co.seoultel.message.mt.mms.core_module.modules.client.ChannelStatus;
import kr.co.seoultel.message.mt.mms.core_module.modules.client.Client;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;

@Slf4j
@Getter
@NoArgsConstructor
public abstract class TcpClient extends Client {

    @Getter @Setter
    public ChannelStatus channelStatus = ChannelStatus.CLOSED;

    public boolean isChannelOpen() {
        return channelStatus.isOpen();
    }

    public boolean isChannelBinding() {
        return channelStatus.isBinding();
    }

    public boolean isChannelBound() {
        return channelStatus.isBound();
    }

    public boolean isChannelClosed() {
        return channelStatus.isClosed();
    }

    public boolean isChannelNotClosed() {
        return channelStatus.isNotClosed();
    }


    protected abstract void init();

    protected abstract void createSession() throws ConnectException;
    protected abstract void closeSession();

    static class HttpClientProperty {
        private String cpid;
        private String bpid;
        private String vasId;
        private String vaspId;
    }
}
