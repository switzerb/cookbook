package com.brennaswitzer.cookbook.sudoku;

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

    private boolean isAllowed(int cell, int candidate) {
        for (int n : getNeighbors(cell)) {
            if (board[n] == candidate) return false;
        }
        return true;
    }

}
