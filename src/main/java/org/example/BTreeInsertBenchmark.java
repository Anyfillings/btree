package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class BTreeInsertBenchmark {

    private BTreeImplementation<Integer, String> tree;
    private Random rnd;

    @Param({"3", "8"}) // разные степени дерева
    private int t;

    @Param({"10000"})  // сколько вставок на итерацию подготовки
    private int preload;

    @Setup(Level.Trial)
    public void setupTrial() {
        rnd = new Random(42);
    }

    @Setup(Level.Invocation)
    public void setupEach() {
        tree = new BTreeImplementation<>(t);
        for (int i = 0; i < preload; i++) {
            tree.insert(i, "v" + i);
        }
    }

    @Benchmark
    public void insertRandom() {
        int key = rnd.nextInt(Integer.MAX_VALUE);
        tree.insert(key, "v" + key);
    }
}
