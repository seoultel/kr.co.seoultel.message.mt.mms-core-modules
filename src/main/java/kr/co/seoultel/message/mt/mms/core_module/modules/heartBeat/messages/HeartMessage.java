package kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.messages;

import io.netty.buffer.ByteBuf;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core_module.modules.heartBeat.HeartBeatProtocol;
import lombok.Builder;


public class HeartMessage extends HeartBeatMessage {

    private String senderName;
    private String senderGroup;
    private String senderChannel;
    private String queueName;
    private String exchangeName;
    private String heartStatus;
    private String expireTime;

    public HeartMessage() {
        this.msgType = HeartBeatProtocol.HEART;
        this.msgLength = String.valueOf(HeartBeatProtocol.HEART_LENGTH);

        this.expireTime = DateUtil.getDate(HeartBeatProtocol.DEFAULT_EXPIRE_TIME);
    }

    @Builder
    public HeartMessage(String senderName, String senderGroup, String senderChannel, String queueName, String exchangeName, String heartStatus) {
        this.msgType = HeartBeatProtocol.HEART;
        this.msgLength = String.valueOf(HeartBeatProtocol.HEART_LENGTH);

        this.senderName = senderName;
        this.senderGroup = senderGroup;
        this.senderChannel = senderChannel;
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.heartStatus = heartStatus;
        this.expireTime = DateUtil.getDate(HeartBeatProtocol.DEFAULT_EXPIRE_TIME);
    }


    @Override
    public void toByteBuf(ByteBuf byteBuf) {
        writeHeader(byteBuf);
        writeBody(byteBuf);
    }

    @Override
    public void fromByteBuf(ByteBuf byteBuf) {
        super.fromByteBuf(byteBuf);

        this.senderName = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.SENDER_NAME_SIZE);         // 10
        this.senderGroup = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.SENDER_GROUP_SIZE);       // 10
        this.senderChannel = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.SENDER_CHANNEL_SIZE);   // 1
        this.queueName = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.QUEUE_NAME_SIZE);           // 40
        this.exchangeName = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.EXCHANGE_NAME_SIZE);     // 20
        this.heartStatus = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.STATUS_SIZE);             // 1
        this.expireTime = ConvertorUtil.getStrPropertyInByteBuf(byteBuf, HeartBeatProtocol.EXPIRE_TIME_SIZE);         // 14
    }

    @Override
    protected void writeBody(ByteBuf byteBuf) {
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(senderName, HeartBeatProtocol.SENDER_NAME_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(senderGroup, HeartBeatProtocol.SENDER_GROUP_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(senderChannel, HeartBeatProtocol.SENDER_CHANNEL_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(queueName, HeartBeatProtocol.QUEUE_NAME_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(exchangeName, HeartBeatProtocol.EXCHANGE_NAME_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(heartStatus, HeartBeatProtocol.STATUS_SIZE));
        byteBuf.writeBytes(ConvertorUtil.convertPropertyToBytesByEucKr(expireTime, HeartBeatProtocol.EXPIRE_TIME_SIZE));
    }

    @Override
    public String toString() {
        return "HeartMessage{" +
                "msgType='" + msgType + '\'' +
                ", msgLength='" + msgLength + '\'' +
                ",senderName='" + senderName + '\'' +
                ", senderGroup='" + senderGroup + '\'' +
                ", senderChannel='" + senderChannel + '\'' +
                ", queueName='" + queueName + '\'' +
                ", exchangeName='" + exchangeName + '\'' +
                ", heartStatus='" + heartStatus + '\'' +
                ", expireTime='" + expireTime + '\'' +
                '}';
    }
}