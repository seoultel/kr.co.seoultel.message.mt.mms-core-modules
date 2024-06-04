package kr.co.seoultel.message.mt.mms.core_module.modules.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultRabbitMQConfig;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq.MsgReportException;
import kr.co.seoultel.message.mt.mms.core_module.modules.consumer.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MrReportService {
    protected transient final Gson gson  = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    protected final AbstractConsumer consumer;
    protected final DefaultRabbitMQConfig rabbitMQConfig;


    public MrReportService(AbstractConsumer consumer, DefaultRabbitMQConfig rabbitMQConfig) {
        this.consumer = consumer;
        this.rabbitMQConfig = rabbitMQConfig;
    }


    public void sendReport(MrReport MrReport) throws MsgReportException {
        try {
            MessageDelivery messageDelivery = MrReport.getMessageDelivery();
            consumer.getChannel().basicPublish(rabbitMQConfig.getMrExchange(), rabbitMQConfig.getMrQueue(), false, null, gson.toJson(messageDelivery).getBytes());
        } catch (Exception e) {
            throw new MsgReportException(e, MrReport);
        }
    }

}
