package kr.co.seoultel.message.mt.mms.core_module.modules.client;

public enum ChannelStatus {
    OPEN("채널 열림"), BINDING("바인딩 중"), BOUND("바인딩 완료"), CLOSED("채널 닫힘");


    private final String statusKor;

    ChannelStatus(String statusKor) {
        this.statusKor = statusKor;
    }

    public boolean isOpen() {
        return this == ChannelStatus.OPEN;
    }

    public boolean isBinding() {
        return this == ChannelStatus.BINDING;
    }

    public boolean isBound() {
        return this == ChannelStatus.BOUND;
    }

    public boolean isClosed() {
        return this == ChannelStatus.CLOSED;
    }

    public boolean isNotClosed() {
        return this != ChannelStatus.CLOSED;
    }
}
