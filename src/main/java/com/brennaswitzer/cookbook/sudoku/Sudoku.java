package com.brennaswitzer.cookbook.sudoku;

import lombok.Getter;

import java.util.Arrays;

public abstract class Sudoku implements Solver {

    private static final char EMPTY_INDICATOR = '.';
    public static final int EMPTY_CELL = 0;

    protected final int[] board;
    protected final int len;
    protected final int dim;
    protected final int boxDim;

    @Getter
    private final boolean solved;

    @Getter
    private final long elapsed;

    @Getter
    private int frameCount = 0;

    public Sudoku(String board) {
        len = board.length();
        dim = (int) Math.sqrt(len);
        assert dim * dim == len;
        boxDim = (int) Math.sqrt(dim);
        assert Math.pow(boxDim, 2) == dim;
        this.board = new int[len];
        for (int i = 0; i < len; i++) {
            char c = board.charAt(i);
            this.board[i] = c == EMPTY_INDICATOR
                    ? EMPTY_CELL
                    : c - '0';
        }
        long start = System.nanoTime();
        this.solved = solve();
        this.elapsed = System.nanoTime() - start;
        if (!solved) {
            System.out.println("Unsolvable puzzle?!");
        }
    }

    protected int findEmptyCell() {
        for (int i = 0; i < len; i++) {
            if (board[i] == EMPTY_CELL) return i;
        }
        return -1;
    }

    protected int idx(int r, int c) {
        return r * dim + c;
    }

    private int[][] neighborCache;

    protected int[] getNeighbors(int cell) {
        if (neighborCache == null) {
            neighborCache = new int[len][];
        }
        if (neighborCache[cell] == null) {
            int[] result = new int[(dim - 1) * 2 + (boxDim - 1) * (boxDim - 1)];
            int idx = 0;

            int row = cell / dim;
            int col = cell % dim;
            for (int i = 0; i < dim; i++) {
                if (i != col) {
                    result[idx++] = idx(row, i);
                }
                if (i != row) {
                    result[idx++] = idx(i, col);
                }
            }

            int boxRow = row - (row % boxDim);
            int boxCol = col - (col % boxDim);
            for (int r = 0; r < boxDim; r++) {
                if (boxRow + r == row) continue;
                for (int c = 0; c < boxDim; c++) {
                    if (boxCol + c == col) continue;
                    result[idx++] = idx(boxRow + r, boxCol + c);
                }
            }

            neighborCache[cell] = result;
        }
        return neighborCache[cell];
    }

    @SuppressWarnings("unused")
    protected int[] getNeighborsForMutation(int cell) {
        int[] ns = getNeighbors(cell);
        return Arrays.copyOf(ns, ns.length);
    }

    protected abstract boolean solve();

    protected void enterFrame() {
        frameCount += 1;
    }

    public final String toString() {
        String b = getBoard();
        StringBuilder sb = new StringBuilder(b);
        sb.append(" (");
        int n = 0;
        for (int i : board) if (i != EMPTY_CELL) n += 1;
        sb.append(n).append(')');
        return sb.toString();
    }

    public final String getBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i : board) {
            sb.append(i == EMPTY_CELL
                    ? EMPTY_INDICATOR
                    : (char) (i + '0'));
        }
        return sb.toString();
    }

}
