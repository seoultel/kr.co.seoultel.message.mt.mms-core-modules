package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.client.ChannelStatus;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.HeartBeatProtocol;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.BeatMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.HeartBeatMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.HeartMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class DefaultHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final DefaultHeartBeatClient heartBeatClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[HEART-BEAT] Successfully activated heart-beat's channel");

        while (!heartBeatClient.sendHeart(ctx)) {
            CommonUtil.doThreadSleep(500L);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[HEART-BEAT] Unfortunately Inactivated heart-beat's channel");
        heartBeatClient.setChannelStatus(ChannelStatus.CLOSED);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        HeartBeatMessage heartBeatMessage = (HeartBeatMessage) message;
        switch (heartBeatMessage.getMsgType()) {
            case HeartBeatProtocol.HEART:
                HeartMessage heartMessage = (HeartMessage) heartBeatMessage;
                log.info("[HEART-BEAT] Successfully received Heart[{}] from \"HEART-BEAT\"", heartMessage);
                break;

            case HeartBeatProtocol.BEAT:
                BeatMessage beatMessage = (BeatMessage) heartBeatMessage;
                log.info("[HEART-BEAT] Successfully received Beat[{}] from \"HEART-BEAT\"", beatMessage);
                break;

            default:
                log.error("???");
                break;
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    break;

                case WRITER_IDLE:
                    if (ctx.channel().isOpen()) heartBeatClient.sendHeart(ctx);
                    break;
            }
        }
    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[HEART-BEAT] uncaught exception in heart-heat", cause);

        if (cause instanceof IOException) {
            heartBeatClient.setChannelStatus(ChannelStatus.CLOSED);
        }
    }
}

