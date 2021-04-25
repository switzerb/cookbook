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

    @Test
    public void _4x4() {
        super._4x4();
    }

    @Test
    public void oneStar() {
        super.oneStar();
    }

    @Test
    public void twoStar() {
        solve(TWO_STAR,
                ".3.9.8...52..43..6.8....9.3.16.9....3.......1....3.62.752364198163879542...215367");
    }

    @Test
    public void beginnerMonster() {
        super.beginnerMonster();
    }

}
