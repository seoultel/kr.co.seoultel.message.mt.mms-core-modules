package kr.co.seoultel.message.mt.mms.core_module.modules.client;

import org.springframework.beans.factory.annotation.Value;

public abstract class Client extends Thread {

    /*
     * USE RABBIT-MQ MT QUEUE
     */
    @Value("${mt.exchange}")
    protected String mtExchangeName;

    @Value("${mt.queue}")
    protected String mtQueueName;

    @Value("${mr.exchange}")
    protected String mrExchangeName;
    @Value("${mr.queue}")
    protected String mrQueueName;


    @Value("${sender.name}")
    protected String name;
    @Value("${sender.group}")
    protected String group;
}
