package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class MatrixBenchmark {

    private static final ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 100)
    public static void singleThreadMultiply(Blackhole blackhole, MatrixParam matrixParam) {
        final int[][] matrixC = MatrixUtil.singleThreadMultiply(matrixParam.matrixA, matrixParam.matrixB);
        blackhole.consume(matrixC);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 100)
    public static void singleThreadMultiplyOpt(Blackhole blackhole, MatrixParam matrixParam) {
        final int[][] matrixC = MatrixUtil.singleThreadMultiplyOpt(matrixParam.matrixA, matrixParam.matrixB);
        blackhole.consume(matrixC);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 100)
    public static void concurrentThreadMultiplyOpt(Blackhole blackhole, MatrixParam matrixParam) {
        final int[][] matrixC = MatrixUtil.concurrentMultiply(matrixParam.matrixA, matrixParam.matrixB, executor);
        executor.shutdown();
        blackhole.consume(matrixC);
    }
}
