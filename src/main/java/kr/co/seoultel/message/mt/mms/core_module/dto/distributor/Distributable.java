package kr.co.seoultel.message.mt.mms.core_module.dto.distributor;

public interface Distributable<T> {

    abstract T get();
    int reset();
}
