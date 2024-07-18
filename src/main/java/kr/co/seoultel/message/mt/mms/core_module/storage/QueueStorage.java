package kr.co.seoultel.message.mt.mms.core_module.storage;

import kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler.FileIOHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class QueueStorage<E> extends Storage<E> {
    private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();

    public QueueStorage(String filePath) {
        super(filePath);
    }

    public QueueStorage(String filePath, FileIOHandler fileIOHandler) {
        super(filePath, fileIOHandler);
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



    public E poll() {
        return queue.poll();
    }


    public E remove() {
        return queue.remove();
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return queue.removeIf(filter);
    }







    @Override
    public Collection<E> snapshot() {
        return new ArrayList<>(queue);
    }

    @Override
    public Storage<E> destroyBy(Supplier<Collection<E>> supplier) throws IOException {
        return super.destroyBy(this::snapshot);
    }
}
