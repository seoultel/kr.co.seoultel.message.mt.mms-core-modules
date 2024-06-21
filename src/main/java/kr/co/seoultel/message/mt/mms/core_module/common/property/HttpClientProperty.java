package kr.co.seoultel.message.mt.mms.core_module.common.property;

import kr.co.seoultel.message.mt.mms.core_module.modules.client.http.entity.CpidInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpClientProperty {

    protected String name;
    protected String group;
    protected String telecom;

    protected CpidInfo cpidInfo;
    protected String bpid;

    protected String vasId;
    protected String vaspId;

    protected String ip;
    protected int port;



    public HttpClientProperty setCpidInfo(CpidInfo cpidInfo) {
        this.cpidInfo = cpidInfo;
        return this;
    }

    @Override
    public String toString() {
        return "HttpClientProperty{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", telecom='" + telecom + '\'' +
                ", cpidInfo=" + cpidInfo +
                ", bpid='" + bpid + '\'' +
                ", vasId='" + vasId + '\'' +
                ", vaspId='" + vaspId + '\'' +
                '}';
    }
}
