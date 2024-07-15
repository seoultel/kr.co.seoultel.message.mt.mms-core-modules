package kr.co.seoultel.message.mt.mms.core_module.distributor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AutoIncreaseNumberDistributor implements Distributable<Integer> {

    private final AtomicInteger current = new AtomicInteger(0);
    private final int length;

    public AutoIncreaseNumberDistributor(int length) {
        this.length = length;
    }

    @Override
    public synchronized Integer get() {
        String cur = String.valueOf(current);
        if (cur.length() >= length) {
            return reset();
        } else {
            return current.addAndGet(1);
        }
    }

    @Override
    public synchronized int reset() {
        current.set(0);
        return current.get();
    }

    public static void main(String[] args) {
        AutoIncreaseNumberDistributor distributor = new AutoIncreaseNumberDistributor(5);
        for (int i = 0; i < 6 ; i++) {
            System.out.println("[current] " + distributor.get());
        }
    }
}
