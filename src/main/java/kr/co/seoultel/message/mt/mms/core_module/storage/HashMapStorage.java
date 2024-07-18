package kr.co.seoultel.message.mt.mms.core_module.storage;


import kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler.FileIOHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;


public class HashMapStorage<K, V> extends Storage<V> {

    private final Map<K, V> map = new ConcurrentHashMap<K, V>();

    public HashMapStorage(String filePath) {
        super(filePath);
    }

    public HashMapStorage(String filePath, FileIOHandler fileIOHandler) {
        super(filePath, fileIOHandler);
    }



    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        return map.containsValue(value);
    }


    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }





    public V replace(K key, V val1) {
        return map.replace(key, val1);
    }

    public boolean replace(K key, V val1, V val2) {

        return map.replace(key, val1, val2);
    }

    public V replaceKey(K oldKey, K newKey) {
        V value = map.remove(oldKey);
        return map.put(newKey, value);
    }

//    public V replaceKey(K oldKey, K newKey, V newValue) {
//        map.remove(oldKey);
//        return map.put(newKey, newValue);
//    }


    public V remove(K key) {
        return map.remove(key);
    }

    public boolean remove(K key, V value) {
        return map.remove(key, value);
    }



    public V get(K key) {
        return map.get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }



    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }


    @Override
    public Collection<V> snapshot() {
        return map.values();
    }
}

