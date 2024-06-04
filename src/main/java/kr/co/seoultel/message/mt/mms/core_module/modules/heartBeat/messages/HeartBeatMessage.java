package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages;

import io.netty.buffer.ByteBuf;
import kr.co.seoultel.message.mt.mms.core.common.interfaces.ConvertableToByteBuf;
import kr.co.seoultel.message.mt.mms.core.messages.Message;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.HeartBeatProtocol;
import lombok.Getter;
import lombok.Setter;


@Setter
public abstract class HeartBeatMessage extends Message implements ConvertableToByteBuf {

    @Getter
    protected String msgType;
    protected String msgLength;

    protected void writeHeader(ByteBuf byteBuf) {
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(msgType, HeartBeatProtocol.MSG_TYPE_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(msgLength, HeartBeatProtocol.MSG_LENGTH_SIZE));
    }

    @Override
    public void fromByteBuf(ByteBuf byteBuf) {
        this.msgType = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.MSG_TYPE_SIZE);
        this.msgLength = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.MSG_LENGTH_SIZE);
    }

    protected abstract void writeBody(ByteBuf byteBuf);
}
