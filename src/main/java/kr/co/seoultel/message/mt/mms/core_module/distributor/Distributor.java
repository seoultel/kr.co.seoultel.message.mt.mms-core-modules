package kr.co.seoultel.message.mt.mms.core_module.distributor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class Distributor<T> implements Distributable<T>{

    @Getter
    protected AtomicInteger index = new AtomicInteger(0);

    @Getter
    protected final int size;
    protected final List<T> origin;

    public Distributor(List<T> origin) {
        this.origin = origin;
        this.size = origin.size();

        log.info("[Distributor] Successfully initiated distributor by data[{}]", origin);
    }

    public abstract T get();

    @Override
    public int reset() {
        this.index.set(0);
        return index.get();
    }
}
