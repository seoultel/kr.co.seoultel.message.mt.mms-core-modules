package kr.co.seoultel.message.mt.mms.core_module.distributor;

public interface Distributable<T> {

    abstract T get();
    int reset();
}
