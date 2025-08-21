package org.example;

import java.util.Optional;

public interface BTree<K extends Comparable<K>, V> {
    Optional<V> search(K key);
    void insert(K key, V value);
    boolean delete(K key);   // TODO: реализовать позже
    int size();              // кол-во уникальных ключей
    int height();            // высота в уровнях: корень=1
}
