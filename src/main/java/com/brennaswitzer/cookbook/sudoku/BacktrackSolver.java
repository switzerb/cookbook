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

    private boolean isAllowed(int cell, int candidate) {
        for (int n : getNeighbors(cell)) {
            if (board[n] == candidate) return false;
        }
        return true;
    }

}
