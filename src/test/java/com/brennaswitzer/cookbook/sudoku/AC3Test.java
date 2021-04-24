package com.brennaswitzer.cookbook.sudoku;

import org.junit.Test;

public class AC3Test extends SudokuTest {

    @Override
    protected Solver getSolver(String puzzle) {
        return new AC3Solver(puzzle);
    }

    @Test
    public void benchmark() {
        benchmark("AC-3 One Star", 100, () ->
                new AC3Solver(ONE_STAR).isSolved());
    }

}
