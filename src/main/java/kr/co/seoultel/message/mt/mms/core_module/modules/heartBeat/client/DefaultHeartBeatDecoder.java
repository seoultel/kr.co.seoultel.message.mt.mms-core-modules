package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.HeartBeatProtocol;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.BeatMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.HeartMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DefaultHeartBeatDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        String msgType = ConvertorUtil.getStrPropertyInByteBuf(in, HeartBeatProtocol.MSG_TYPE_SIZE);
        in.resetReaderIndex();

        switch (msgType) {
            case HeartBeatProtocol.HEART:
                HeartMessage heartMessage = new HeartMessage();
                heartMessage.fromByteBuf(in);

                out.add(heartMessage);
                break;

            case HeartBeatProtocol.BEAT:
                BeatMessage beatMessage = new BeatMessage();
                beatMessage.fromByteBuf(in);

                out.add(beatMessage);
                break;

            default:
                log.error("??????");
                break;
        }
    }
}
