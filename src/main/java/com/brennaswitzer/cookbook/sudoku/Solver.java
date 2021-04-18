package com.brennaswitzer.cookbook.sudoku;

public interface Solver {

    boolean isSolved();

    String getSolution();

    long getElapsed();

    int getFrameCount();

}
