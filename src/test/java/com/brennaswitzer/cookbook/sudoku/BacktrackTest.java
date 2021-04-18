package com.brennaswitzer.cookbook.sudoku;

public class BacktrackTest extends SudokuTest {

    @Override
    protected Solver getSolver(String puzzle) {
        return new BacktrackSolver(puzzle);
    }

}
