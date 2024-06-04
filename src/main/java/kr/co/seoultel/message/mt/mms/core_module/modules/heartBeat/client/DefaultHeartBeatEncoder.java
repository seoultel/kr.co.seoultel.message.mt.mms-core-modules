package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.HeartBeatMessage;

public class DefaultHeartBeatEncoder extends MessageToByteEncoder<HeartBeatMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, HeartBeatMessage in, ByteBuf out) throws Exception {
        in.toByteBuf(out);
    }
}
