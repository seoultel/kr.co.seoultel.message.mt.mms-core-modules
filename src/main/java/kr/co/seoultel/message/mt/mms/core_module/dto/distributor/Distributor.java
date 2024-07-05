package kr.co.seoultel.message.mt.mms.core_module.dto.distributor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
abstract class Distributor<T> implements Distributable<T>{

    @Getter
    protected AtomicInteger index = new AtomicInteger(0);

    @Getter
    protected final int size;
    protected final List<T> list;

    public Distributor(List<T> list) {
        this.list = list;
        this.size = list.size();

        log.info("[Distributor] Successfully initiated distributor by data[{}]", list);
    }

    public abstract T get();

    @Override
    public int reset() {
        this.index.set(0);
        return index.get();
    }
}
