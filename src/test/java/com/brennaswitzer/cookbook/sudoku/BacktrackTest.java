package com.brennaswitzer.cookbook.sudoku;

import org.junit.Test;

public class BacktrackTest extends SudokuTest {

    @Override
    protected Solver getSolver(String puzzle) {
        return new BacktrackSolver(puzzle);
    }

    @Test
    public void benchmarkOneStar() {
        benchmark("Backtrack One Star", 1000, () ->
                new BacktrackSolver(ONE_STAR).isSolved());
    }

}
