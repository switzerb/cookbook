package com.brennaswitzer.cookbook.sudoku;

public class BacktrackSolver extends Sudoku {

    BacktrackSolver(String board) {
        super(board);
    }

    private int idx(int r, int c) {
        return r * dim + c;
    }

    private int findEmptyCell() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == EMPTY) return i;
        }
        return -1;
    }

    protected boolean solve() {
        int i = findEmptyCell();
        if (i < 0) return true;
        enterFrame();
        for (int n = 1; n <= dim; n++) {
            char c = (char) ('0' + n);
            if (isAllowed(i, c)) {
                board[i] = c;
                if (solve()) return true;
                board[i] = EMPTY;
            }
        }
        return false;
    }

    private boolean isAllowed(int cell, char candidate) {
        int row = cell / dim;
        int col = cell % dim;
        for (int i = 0; i < dim; i++) {
            if (board[idx(row, i)] == candidate || board[idx(i, col)] == candidate) {
                return false;
            }
        }

        int boxRow = row - (row % boxDim);
        int boxCol = col - (col % boxDim);
        for (int r = 0; r < boxDim; r++) {
            for (int c = 0; c < boxDim; c++) {
                if (board[idx(boxRow + r, boxCol + c)] == candidate) {
                    return false;
                }
            }
        }

        return true;
    }

}
