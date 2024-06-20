package kr.co.seoultel.message.mt.mms.core_module.modules.client.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;

@Slf4j
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpClientProperty {

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

    @Value("${sender.telecom}")
    protected String telecom;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setGroup(String group) {
        this.group = group.toUpperCase();
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom.toUpperCase();
    }


    @Value("${sender.http.bpid}")
    protected String bpid;

    protected String cpid;

    public HttpClientProperty setCpid(String cpid) {
        this.cpid = cpid;
        return this;
    }

    @Value("${sender.http.vas-id}")
    protected String vasId;

    @Value("${sender.http.vasp-id}")
    protected String vaspId;

    @Override
    public String toString() {
        return "HttpClientProperty{" +
                "mtExchangeName='" + mtExchangeName + '\'' +
                ", mtQueueName='" + mtQueueName + '\'' +
                ", mrExchangeName='" + mrExchangeName + '\'' +
                ", mrQueueName='" + mrQueueName + '\'' +
                ", name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", telecom='" + telecom + '\'' +
                ", bpid='" + bpid + '\'' +
                ", cpid='" + cpid + '\'' +
                ", vasId='" + vasId + '\'' +
                ", vaspId='" + vaspId + '\'' +
                '}';
    }
}
