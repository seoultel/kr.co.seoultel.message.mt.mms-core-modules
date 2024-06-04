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

//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
//        BeatMessage beat = new BeatMessage();
//        switch (state()) {
//            // header
//            case READ_MSG_TYPE:
//                ByteBuf msgTypeBuf = in.readBytes(HeartBeatProtocol.MSG_TYPE_SIZE);
//                String msgType = msgTypeBuf.toString(Charset.forName("euc-kr"));
//
//                beat.setMsgType(msgType);
//
//                msgTypeBuf.release();
//                checkpoint(HeartBeat.READ_MSG_LENGTH);
//                break;
//
//            case READ_MSG_LENGTH:
//                ByteBuf msgLengthBuf = in.readBytes(HeartBeatProtocol.MSG_LENGTH_SIZE);
//                String msgLength = msgLengthBuf.toString(Charset.forName("euc-kr"));
//
//                beat.setMsgLength(msgLength);
//
//                msgLengthBuf.release();
//                checkpoint(HeartBeat.READ_STATUS);
//
//                break;
//
//            // body
//            case READ_STATUS:
//                ByteBuf beatStatusBuf = in.readBytes(HeartBeatProtocol.STATUS_SIZE);
//                String beatStatus = beatStatusBuf.toString(Charset.forName("euc-kr"));
//
//                beat.setBeatStatus(beatStatus);
//                out.add(beat);
//
//                beatStatusBuf.release();
//                checkpoint(HeartBeat.READ_MSG_TYPE);
//                break;
//
//            default:
//                throw new IllegalStateException("Unexpected value: " + state());
//        }
//    }
}
