package com.brennaswitzer.cookbook.sudoku;

public class AC3Test extends SudokuTest {

    @Override
    protected Solver getSolver(String puzzle) {
        return new AC3Solver(puzzle);
    }

}
