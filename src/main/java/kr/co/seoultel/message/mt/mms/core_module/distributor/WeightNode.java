package kr.co.seoultel.message.mt.mms.core_module.distributor;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WeightNode<E> {
    protected int weight;
    protected E element;

    public WeightNode(int weight, E element) {
        this.weight = weight;
        this.element = element;
    }
}
