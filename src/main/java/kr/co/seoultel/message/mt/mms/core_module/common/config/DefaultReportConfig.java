package kr.co.seoultel.message.mt.mms.core_module.common.config;


import kr.co.seoultel.message.mt.mms.core_module.modules.consumer.AbstractConsumer;

public abstract  class DefaultReportConfig {

    protected final AbstractConsumer consumer;
    protected final DefaultRabbitMQConfig rabbitMQConfig;

    public DefaultReportConfig(AbstractConsumer consumer, DefaultRabbitMQConfig rabbitMQConfig) {
        this.consumer = consumer;
        this.rabbitMQConfig = rabbitMQConfig;
    }
}
