package kr.co.seoultel.message.mt.mms.core_module.storage;

import kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler.FileIOHandler;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;

public class SortedQueueStorage<E> extends Storage<E> {
    protected final ConcurrentSkipListSet<E> queue;

    public SortedQueueStorage(String filePath) {
        super(filePath);
        queue = new ConcurrentSkipListSet<E>();
    }

    public SortedQueueStorage(String filePath, Comparator<? super E> comparator) {
        super(filePath);
        queue = new ConcurrentSkipListSet<E>(comparator);
    }

    public SortedQueueStorage(String filePath, FileIOHandler fileIOHandler, Comparator<? super E> comparator) {
        super(filePath, fileIOHandler);
        queue = new ConcurrentSkipListSet<E>(comparator);
    }



    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean contains(E e) {
        return queue.contains(e);
    }


    public boolean add(E e) {
        return queue.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        return queue.addAll(c);
    }



    public E pollFirst() {
        return queue.pollFirst();
    }

    public E pollLast() {
        return queue.pollLast();
    }


    public boolean remove(E element) {
        return queue.remove(element);
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return queue.removeIf(filter);
    }


    @Override
    Collection<E> snapshot() {
        return queue;
    }
}
