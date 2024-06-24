package kr.co.seoultel.message.mt.mms.core_module.dto.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Getter @Setter
public class Endpoint {

    protected String ip;
    protected int port;
    protected final String url;

    public Endpoint(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.url = "";
    }

    public Endpoint(String ip, int port, String url) {
        this.ip = ip;
        this.port = port;
        this.url = url;
    }

    public String getHttpUrl() {
        return String.format("http://%s:%d", ip, port) + url;
    }

    public String getHttpsUrl() {
        return String.format("https://%s:%d", ip, port) + url;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    public static void main(String[] args) {
        Endpoint endpoint = new Endpoint("ip", 3030);
        System.out.println(endpoint.getHttpsUrl());
    }
}
