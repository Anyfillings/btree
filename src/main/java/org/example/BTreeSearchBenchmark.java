package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class BTreeSearchBenchmark {

    private BTreeImplementation<Integer, String> tree;
    private Random rnd;

    @Param({"10000", "100000"})
    private int n;

    @Setup(Level.Iteration)
    public void setup() {
        tree = new BTreeImplementation<>(3);
        rnd = new Random(42);
        for (int i = 0; i < n; i++) {
            tree.insert(i, "v" + i);
        }
    }

    @Benchmark
    public String searchHit() {
        return tree.search(rnd.nextInt(n)).orElse(null);
    }

    @Benchmark
    public String searchMiss() {
        return tree.search(n + rnd.nextInt(n)).orElse(null);
    }
}
