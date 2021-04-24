package com.brennaswitzer.cookbook.sudoku;

import lombok.Getter;

public abstract class Sudoku implements Solver {

    private static final char EMPTY_INDICATOR = '.';
    public static final int EMPTY_CELL = 0;

    protected final int[] board;
    protected final int dim;
    protected final int boxDim;

    @Getter
    private final boolean solved;

    @Getter
    private final long elapsed;

    @Getter
    private int frameCount = 0;

    public Sudoku(String board) {
        int len = board.length();
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

    protected abstract boolean solve();

    protected void enterFrame() {
        frameCount += 1;
    }

    public final String toString() {
        return getSolution();
    }

    public final String getSolution() {
        StringBuilder sb = new StringBuilder();
        for (int i : board) {
            sb.append(i == EMPTY_CELL
                    ? EMPTY_INDICATOR
                    : (char) (i + '0'));
        }
        return sb.toString();
    }

}
