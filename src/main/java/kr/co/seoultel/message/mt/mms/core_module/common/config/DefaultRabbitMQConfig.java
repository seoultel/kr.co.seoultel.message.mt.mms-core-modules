package kr.co.seoultel.message.mt.mms.core_module.common.config;


import kr.co.seoultel.message.mt.mms.core.common.interfaces.Checkable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Getter @Setter
public abstract class DefaultRabbitMQConfig implements Checkable {

    @Value("${mt.exchange}")
    protected String mtExchange;
    @Value("${mt.queue}")
    protected String mtQueue;

    @Value("${mr.exchange}")
    protected String mrExchange;
    @Value("${mr.queue}")
    protected String mrQueue;

    @Getter
    protected boolean primaryCluster;
    protected String primaryHost;
    protected int primaryPort;
    protected String primaryUsername;
    protected String primaryPassword;
    protected String primaryVirtualHost;

    // Secondary RabbitMQ Setting
    @Getter
    protected boolean secondaryCluster;
    protected String secondaryHost;
    protected int secondaryPort;
    protected String secondaryUsername;
    protected String secondaryPassword;
    protected String secondaryVirtualHost;

    public void check(){
        log.info("[RABBITMQ / PRIMARY] -> cluster : \"{}\", host : \"{}\", port : \"{}\", username : \"{}\", password : \"{}\", virtual-host : \"{}\"",
                primaryCluster,  primaryHost, primaryPort, primaryUsername, primaryPassword, primaryVirtualHost);

        log.info("[RABBITMQ / SECONDARY] -> cluster : \"{}\", host : \"{}\", port : \"{}\", username : \"{}\", password : \"{}\", virtual-host : \"{}\"",
                secondaryCluster,  secondaryHost, secondaryPort, secondaryUsername, secondaryPassword, secondaryVirtualHost);

        log.info("[RABBITMQ / PRIMARY | SECONDARY] -> MT[exchange : \"{}\" | queue : \"{}\"], MR[exchange : \"{}\" | queue : \"{}\"]",
                mtExchange, mtQueue, mrExchange, mrQueue);
    }
}
