package org.example;

import java.util.List;
import java.util.Optional;

public class BTreeImplementation<K extends Comparable<K>, V> implements BTree<K, V> {

    private final int t; // минимальная степень (t >= 2)
    private BTreeNode<K, V> root;
    private int size;

    public BTreeImplementation(int minDegree) {
        if (minDegree < 2) throw new IllegalArgumentException("minDegree must be >= 2");
        this.t = minDegree;
        this.root = new BTreeNode<>(true, t);
        this.size = 0;
    }

    @Override
    public Optional<V> search(K key) {
        return searchNode(root, key);
    }

    private Optional<V> searchNode(BTreeNode<K, V> x, K key) {
        int i = lowerBound(x.keys, key);
        if (i < x.keyCount() && key.compareTo(x.keys.get(i)) == 0) {
            return Optional.ofNullable(x.values.get(i));
        }
        if (x.leaf) return Optional.empty();
        return searchNode(x.children.get(i), key);
    }

    @Override
    public void insert(K key, V value) {
        // если ключ уже существует — обновляем значение без изменения size
        if (replaceIfExists(root, key, value)) return;

        if (root.isFull()) {
            // создаём новый корень и сплитим старый
            BTreeNode<K, V> s = new BTreeNode<>(false, t);
            s.children.add(root);
            splitChild(s, 0);
            root = s;
        }
        insertNonFull(root, key, value);
        size++;
    }

    private boolean replaceIfExists(BTreeNode<K,V> x, K key, V value) {
        int i = lowerBound(x.keys, key);
        if (i < x.keyCount() && key.compareTo(x.keys.get(i)) == 0) {
            x.values.set(i, value);
            return true;
        }
        if (x.leaf) return false;
        return replaceIfExists(x.children.get(i), key, value);
    }

    // Сплит ребёнка x.children[i], который полон
    private void splitChild(BTreeNode<K, V> x, int i) {
        BTreeNode<K, V> y = x.children.get(i);     // полный узел
        BTreeNode<K, V> z = new BTreeNode<>(y.leaf, t);

        // перемещаем t-1 правых ключей/значений в z
        for (int j = 0; j < t - 1; j++) {
            z.keys.add(y.keys.remove(t));          // после удаления медианы в конце останется сдвиг
            z.values.add(y.values.remove(t));
        }

        // если не лист — переносим t правых детей
        if (!y.leaf) {
            for (int j = 0; j < t; j++) {
                z.children.add(y.children.remove(t));
            }
        }

        // медианный ключ/значение поднимаем в x
        K midKey = y.keys.remove(t - 1);
        V midVal = y.values.remove(t - 1);

        x.children.add(i + 1, z);
        x.keys.add(i, midKey);
        x.values.add(i, midVal);
    }

    private void insertNonFull(BTreeNode<K, V> x, K key, V value) {
        int i = x.keyCount() - 1;

        if (x.leaf) {
            // найдём позицию и вставим
            int pos = lowerBound(x.keys, key);
            x.keys.add(pos, key);
            x.values.add(pos, value);
        } else {
            int pos = lowerBound(x.keys, key);
            BTreeNode<K, V> child = x.children.get(pos);
            if (child.isFull()) {
                splitChild(x, pos);
                // после split сравним с поднятым ключом
                int cmp = key.compareTo(x.keys.get(pos));
                if (cmp > 0) pos++;
            }
            insertNonFull(x.children.get(pos), key, value);
        }
    }

    // нижняя граница: первый индекс >= key
    private int lowerBound(List<K> keys, K key) {
        int l = 0, r = keys.size();
        while (l < r) {
            int m = (l + r) >>> 1;
            int cmp = key.compareTo(keys.get(m));
            if (cmp <= 0) r = m; else l = m + 1;
        }
        return l;
    }

    @Override
    public boolean delete(K key) {
        // Реализация удаления в B-дереве объемная (слияния/заимствования).
        // Добавим отдельно, когда зафиксируем вставку/поиск и снимем бенчи.
        throw new UnsupportedOperationException("Delete is not implemented yet");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        int h = 0;
        BTreeNode<K,V> x = root;
        while (x != null) {
            h++;
            if (x.leaf) break;
            x = x.children.get(0);
        }
        return h; // уровни: корень = 1
    }
}
