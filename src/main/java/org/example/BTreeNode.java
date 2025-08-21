package org.example;

import java.util.ArrayList;
import java.util.List;

class BTreeNode<K extends Comparable<K>, V> {
    boolean leaf;
    final int t; // минимальная степень
    final List<K> keys = new ArrayList<>();
    final List<V> values = new ArrayList<>();
    final List<BTreeNode<K, V>> children = new ArrayList<>();

    BTreeNode(boolean leaf, int t) {
        this.leaf = leaf;
        this.t = t;
    }

    int keyCount() { return keys.size(); }

    boolean isFull() { return keyCount() == (2 * t - 1); }
}
