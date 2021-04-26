package com.brennaswitzer.cookbook.sudoku;

import org.junit.Test;

public class DomainBacktrackTest extends SudokuTest {

    @Override
    protected Solver getSolver(String puzzle) {
        return new DomainBacktrackSolver(puzzle);
    }

    @Test
    public void benchmarkFiveStar() {
        benchmark("Domain Backtrack Five Star", 1000, () ->
                new BacktrackSolver(FIVE_STAR).isSolved());
    }

    @Test
    public void benchmarkTableFive() {
        benchmark("Domain Backtrack Table Five", 10, () ->
                new BacktrackSolver(TABLE_FIVE).isSolved());
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

    @Test
    public void beginnerMonster() {
        super.beginnerMonster();
    }

    @Test
    public void expertMonster() {
        super.expertMonster();
    }
}
