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

    @Test
    public void _4x4() {
        super._4x4();
    }

    @Test
    public void blank() {
        super.blank();
    }

    @Test
    public void oneStar() {
        super.oneStar();
    }

    @Test
    public void twoStar() {
        super.twoStar();
    }

    @Test
    public void threeStar() {
        super.threeStar();
    }

    @Test
    public void fourStar() {
        super.fourStar();
    }

    @Test
    public void fiveStar() {
        super.fiveStar();
    }

    @Test
    public void tableTwo() {
        super.tableTwo();
    }

    @Test
    public void tableFive() {
        super.tableFive();
    }

    // the monsters take at least many minutes, perhaps hours, to backtrack

}
