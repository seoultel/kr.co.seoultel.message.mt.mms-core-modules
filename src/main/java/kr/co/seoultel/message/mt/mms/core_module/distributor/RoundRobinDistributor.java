package kr.co.seoultel.message.mt.mms.core_module.distributor;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class RoundRobinDistributor<T> extends Distributor<T> {

    public RoundRobinDistributor(List<T> list) {
        super(list);
    }

    public T get() {
        int currentIndex = index.getAndUpdate(i -> (i + 1) % size);
        return origin.get(currentIndex);
    }

    public Collection<T> getList() {
        return origin;
    }


    public static void main(String[] args) {
        List list = List.of(1,2,3,4,5);
        RoundRobinDistributor<Integer> distributor = new RoundRobinDistributor(list);
        IntStream.range(1, 10000).parallel().forEach((d) -> {
            System.out.println(distributor.get());
        });
    }
}
