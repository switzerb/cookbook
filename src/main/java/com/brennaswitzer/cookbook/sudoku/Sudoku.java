package com.brennaswitzer.cookbook.sudoku;

import lombok.Getter;

public abstract class Sudoku implements Solver {

    public static final char EMPTY = '.';

    protected final char[] board;
    protected final int dim;
    protected final int boxDim;

    @Getter
    private final boolean solved;

    @Getter
    private final long elapsed;

    @Getter
    private int frameCount = 0;

    Sudoku(String board) {
        int len = board.length();
        dim = (int) Math.sqrt(len);
        assert dim * dim == len;
        boxDim = (int) Math.sqrt(dim);
        assert Math.pow(boxDim, 2) == dim;
        this.board = board.toCharArray();
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
        return new String(board);
    }

}
