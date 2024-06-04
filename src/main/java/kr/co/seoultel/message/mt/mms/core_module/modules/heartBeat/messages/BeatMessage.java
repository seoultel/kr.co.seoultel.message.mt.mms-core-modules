package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages;

import io.netty.buffer.ByteBuf;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.HeartBeatProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class BeatMessage extends HeartBeatMessage {
    private String beatStatus;

    public BeatMessage() {
        this.msgType = HeartBeatProtocol.BEAT;
        this.msgLength = String.valueOf(HeartBeatProtocol.STATUS_SIZE);
    }

    @Builder
    public BeatMessage(String beatStatus) {
        this.msgType = HeartBeatProtocol.BEAT;
        this.msgLength = String.valueOf(HeartBeatProtocol.STATUS_SIZE);

        this.beatStatus = beatStatus;
    }

    @Override
    protected void writeBody(ByteBuf byteBuf) {
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(beatStatus, HeartBeatProtocol.BEAT_LENGTH));
    }


    @Override
    public void toByteBuf(ByteBuf byteBuf) {
        writeHeader(byteBuf);
        writeBody(byteBuf);
    }

    @Override
    public void fromByteBuf(ByteBuf byteBuf) {
        super.fromByteBuf(byteBuf);
        this.beatStatus = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.STATUS_SIZE);
    }

    @Override
    public String toString() {
        return "BeatMessage{" +
                "msgType='" + msgType + '\'' +
                ", msgLength='" + msgLength + '\'' +
                ", beatStatus='" + beatStatus + '\'' +
                '}';
    }
}
