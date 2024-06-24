package kr.co.seoultel.message.mt.mms.core_module.dto.distributor;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class RoundRobinDistributor<T> extends Distributor<T> {

    public RoundRobinDistributor(List<T> list) {
        super(list);
    }

    @Override
    public T get() {
        while (true) {
            int currentIndex = index.getAndIncrement();
            if (currentIndex >= size) {
                synchronized (this) {
                    if (index.get() >= size) {
                        reset();
                        currentIndex = index.getAndIncrement();
                    } else {
                        currentIndex = index.getAndIncrement();
                    }
                }
            }
            return list.get(currentIndex % size);
        }
    }


    public static void main(String[] args) {
        List list = List.of(1,2,3,4,5);
        RoundRobinDistributor<Integer> distributor = new RoundRobinDistributor(list);
        IntStream.range(1, 10000).parallel().forEach((d) -> {
            System.out.println(distributor.get());
        });
    }
}
