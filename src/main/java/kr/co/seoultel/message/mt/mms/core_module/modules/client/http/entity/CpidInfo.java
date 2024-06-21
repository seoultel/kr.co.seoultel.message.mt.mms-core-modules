package kr.co.seoultel.message.mt.mms.core_module.modules.client.http.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class CpidInfo {

    private String cpid;
    private int tps;

    @Override
    public String toString() {
        return "Cpid{" +
                    "cid='" + cpid + '\'' +
                    ", tps=" + tps +
                '}';
    }
}
