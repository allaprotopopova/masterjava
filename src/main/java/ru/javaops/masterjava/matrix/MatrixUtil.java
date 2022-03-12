package ru.javaops.masterjava.matrix;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) {
        final int matrixSize = matrixA.length;
        ExecutorCompletionService<List<ResultMatrixItem>> completionService = new ExecutorCompletionService<>(executor);

        List<Future<List<ResultMatrixItem>>> futures = IntStream.range(0, matrixB.length)
                .mapToObj(i -> completionService.submit(() -> singleColumnMultiply(matrixA, matrixB, i)))
                .collect(Collectors.toList());

        final int[][] matrixC = new int[matrixSize][matrixSize];

        while (!futures.isEmpty()) {
            Future<List<ResultMatrixItem>> completedFuture = null;
            try {
                completedFuture = completionService.poll(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (completedFuture == null) {
                throw new RuntimeException("cannot count a result matrix");
            }

            try {
                completedFuture.get().forEach(item -> matrixC[item.rowNumber][item.columnNumber] = item.value);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("cannot count a result matrix");
            }
            futures.remove(completedFuture);

        }
        return matrixC;
    }

    private static List<ResultMatrixItem> singleColumnMultiply(int[][] matrixA, int[][] matrixB, int colNumber) {

        final int matrixSize = matrixA.length;
        final int[] thatCol = new int[matrixSize];
        for (int k = 0; k < matrixSize; k++) {
            thatCol[k] = matrixB[k][colNumber];
        }

        return IntStream.range(0, matrixSize)
                .mapToObj(rowNum -> {
                    int[] thisRow = matrixA[rowNum];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += thisRow[k] * thatCol[k];
                    }
                    return new ResultMatrixItem(rowNum, colNumber, sum);
                })
                .collect(Collectors.toList());

    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static int[][] singleThreadMultiplyOpt(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[] thatCol = new int[matrixSize];
        try {
            for (int i = 0; ; i++) {
                for (int k = 0; k < matrixSize; k++) {
                    thatCol[k] = matrixB[k][i];
                }

                for (int j = 0; j < matrixSize; j++) {
                    int[] thisRow = matrixA[j];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += thisRow[k] * thatCol[k];
                    }
                    matrixC[j][i] = sum;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //nothing to do
        }
        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }


    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}

class ResultMatrixItem {
    int rowNumber;
    int columnNumber;
    int value;

    public ResultMatrixItem(int rowNumber, int columnNumber, int value) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.value = value;
    }
}
