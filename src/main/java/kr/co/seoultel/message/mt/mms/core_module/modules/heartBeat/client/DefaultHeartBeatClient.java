package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.client.ChannelStatus;
import kr.co.seoultel.message.mt.mms.core_module.modules.client.TcpClient;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultHeartBeatConfig;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.HeartBeatProtocol;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages.HeartMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class DefaultHeartBeatClient extends TcpClient {

    /**************************************
     *              Variables             *
     **************************************/
    protected Channel channel;

    @Getter @Setter
    protected static String hStatus = HeartBeatProtocol.HEART_SUCCESS;



    protected final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);


    protected final Bootstrap clientBootstrap = new Bootstrap().group(eventLoopGroup)
                                                            .channel(NioSocketChannel.class)
                                                            .option(ChannelOption.TCP_NODELAY, false)
                                                            .option(ChannelOption.SO_KEEPALIVE, false)
                                                            .option(ChannelOption.SO_REUSEADDR, true);

    @Override
    protected void init() {
    }

    @Override
    protected void createSession() {
        DefaultHeartBeatHandler heartBeatHandler = new DefaultHeartBeatHandler(this);
        clientBootstrap.remoteAddress(DefaultHeartBeatConfig.HOST, DefaultHeartBeatConfig.PORT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(60, 25, 0))
                                .addLast(new DefaultHeartBeatEncoder())
                                .addLast(new DefaultHeartBeatDecoder())
                                .addLast(heartBeatHandler);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        log.error("[HEART-BEAT] Fail to create session to HeartBeat");
                    }
                });
    }

    public void connectTo() {
        try {
            channel = clientBootstrap.connect().addListener(future -> {
                if (future.isSuccess()) {
                    setChannelStatus(ChannelStatus.BOUND);
                    log.info("[HEART-BEAT] Successfully connected to host[HEART-BEAT : \"{}:{}\"]", DefaultHeartBeatConfig.HOST, DefaultHeartBeatConfig.PORT);
                } else {
                    log.info("[HEART-BEAT] Fail to connect to host[HEART-BEAT : \"{}:{}\"]", DefaultHeartBeatConfig.HOST, DefaultHeartBeatConfig.PORT);
                }
            }).syncUninterruptibly().channel();
        } catch (Exception e) {
            log.error("[HEART-BEAT] Fail to connect to host[HEART-BEAT : \"{}:{}\"]", DefaultHeartBeatConfig.HOST, DefaultHeartBeatConfig.PORT, e);
        }
    }


    public boolean sendHeart(Channel channel) {
        HeartMessage heart = getHeartInstance(name, group);
        return channel.writeAndFlush(heart).addListener(future -> {
            if (future.isSuccess()) {
                log.info("[HEART-BEAT] Successfully sent Message[{}] to HEART-BEAT", heart);
            } else {
                log.error("[HEART-BEAT] Failed to send Message[{}] to HEART-BEAT", heart);
            }
        }).syncUninterruptibly().isSuccess();
    }

    public boolean sendHeart(ChannelHandlerContext ctx) {
        HeartMessage heart = getHeartInstance(name, group);
        return ctx.writeAndFlush(heart).addListener(future -> {
            if (future.isSuccess()) {
                log.info("[HEART-BEAT] Successfully sent Message[{}] to HEART-BEAT", heart);
            } else {
                log.error("[HEART-BEAT] Failed to send Message[{}] to HEART-BEAT", heart);
            }
        }).syncUninterruptibly().isSuccess();
    }


    @Override
    protected void closeSession() {
        if (channel != null) {
            try {
                channel.close();
                setChannelStatus(ChannelStatus.CLOSED);
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        }

        log.info("[HEART-BEAT] Successfully closed session of heart-Beat");
    }


    protected HeartMessage getHeartInstance(String name, String group){
        String msgChannel = name.split("_")[1]; // S or M
        return HeartMessage.builder()

                .senderName(name.toUpperCase())
                .senderGroup(group.toUpperCase())
                .senderChannel(msgChannel)
                .queueName(mtQueueName)
                .exchangeName(mtExchangeName)
                .heartStatus(hStatus)
                .expireTime(DateUtil.getDate(HeartBeatProtocol.DEFAULT_EXPIRE_TIME)).build();
    }


    public void destroy() {
        this.interrupt();
        closeSession();
    }
}

