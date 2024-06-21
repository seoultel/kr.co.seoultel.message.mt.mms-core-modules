package kr.co.seoultel.message.mt.mms.core_module.common.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Getter @Setter
@NoArgsConstructor
public class RabbitMqProperty {

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
}
