package kr.co.seoultel.message.mt.mms.core_module.distributor;

import java.util.*;

public class WeightedRoundRobinDistributor<T extends WeightNode<?>> extends Distributor<T> {
    private final int maxWeight;
    private int gcdWeight;
    private int currentIndex = -1;
    private int currentWeight = 0;

    public WeightedRoundRobinDistributor(List<T> origin) {
        super(origin);
        this.maxWeight = origin.stream().mapToInt(WeightNode::getWeight).max().orElse(0);
        this.gcdWeight = getGcdWeight(origin);
    }

    @Override
    public T get() {
        while (true) {
            currentIndex = (currentIndex + 1) % origin.size();
            if (currentIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if (currentWeight == 0)
                        return null;
                }
            }
            if (origin.get(currentIndex).getWeight() >= currentWeight) {
                return origin.get(currentIndex);
            }
        }
    }

    @Override
    public int reset() {
        currentWeight = 0;
        index.set(0);

        return index.get();
    }

    private int getGcdWeight(List<T> servers) {
        int w = 0;
        for (WeightNode<?> s : servers) {
            w = gcd(w, s.getWeight());
        }
        return w;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }



//    public static void main(String[] args) {
//        // Create a list of WeightNode instances with weights
//        List<WeightNode<String>> nodes = new ArrayList<>();
//        nodes.add(new WeightNode<>(5, "A"));
//        nodes.add(new WeightNode<>(9, "B"));
//        nodes.add(new WeightNode<>(7, "C"));
//        nodes.add(new WeightNode<>(10, "D"));
//
//        // 5 + 9 + 7 + 10  = 31
//        // Create an instance of WeightedRoundRobinDistributor
//        WeightedRoundRobinDistributor<WeightNode<String>> distributor = new WeightedRoundRobinDistributor<>(nodes);
//
//        // Test by selecting elements multiple times
//        int totalSelects = 20; // Total number of selections to test
//
//        Map<String, Integer> count = new HashMap<>();
//        System.out.println("Testing weighted round-robin distribution:");
//        for (int i = 0; i < 15000; i++) {
//            WeightNode<String> selectedNode = distributor.get();
//            count.put(selectedNode.getElement(), count.getOrDefault(selectedNode.getElement(), 0) + 1);
//            // System.out.println("Selected: " + selectedNode);
//        }
//
//        System.out.println(count);
//    }
}
