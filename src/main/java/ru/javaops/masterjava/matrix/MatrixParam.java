package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class MatrixParam {

    @Param({"1000"})
    public int matrixSize;

    public int[][] matrixA;
    public int[][] matrixB;


    @Setup(Level.Invocation)
    public void setUp() {
        matrixA = MatrixUtil.create(matrixSize);
        matrixB = MatrixUtil.create(matrixSize);
    }
}
