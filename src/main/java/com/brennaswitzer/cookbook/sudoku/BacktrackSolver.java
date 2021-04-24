package com.brennaswitzer.cookbook.sudoku;

import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class BacktrackSolver extends Sudoku {

    BacktrackSolver(String board) {
        super(board);
    }

    protected boolean solve() {
        int i = findEmptyCell();
        if (i < 0) return true;
        enterFrame();
        for (int n = 1; n <= dim; n++) {
            if (isAllowed(i, n)) {
                board[i] = n;
                if (solve()) return true;
                board[i] = EMPTY_CELL;
            }
        }
        return false;
    }

    private class Neighbors implements Spliterator.OfInt {
        final int cell;
        final int row;
        final int col;
        final int boxRow;
        final int boxCol;

        int r, c, br, bc;

        private Neighbors(int cell) {
            this.cell = cell;
            this.row = cell / dim;
            this.col = cell % dim;
            boxRow = row - (row % boxDim);
            boxCol = col - (col % boxDim);
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            int n;
            // row
            while (c < dim) {
                n = idx(row, c);
                c += 1;
                if (n != cell) {
                    action.accept(n);
                    return true;
                }
            }

            // col
            while (r < dim) {
                n = idx(r, col);
                r += 1;
                if (n != cell) {
                    action.accept(n);
                    return true;
                }
            }

            // box
            for (; br < boxDim; br++) {
                if (boxRow + br == row) continue;
                while (bc < boxDim) {
                    if (boxCol + bc == col) {
                        bc += 1;
                        continue;
                    }
                    n = idx(boxRow + br, boxCol + bc);
                    bc += 1;
                    if (n != cell) {
                        action.accept(n);
                        return true;
                    }
                }
                bc = 0;
            }
            return false;
        }

        @Override
        public OfInt trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return IMMUTABLE;
        }
    }

    protected IntStream neighbors(int cell) {
        return StreamSupport.intStream(new Neighbors(cell), false);
    }

    private boolean isAllowed(int cell, int candidate) {
        return !neighbors(cell)
                .filter(n -> board[n] == candidate)
                .findAny()
                .isPresent();
    }

}
