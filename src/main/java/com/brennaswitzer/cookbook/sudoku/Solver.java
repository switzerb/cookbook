package com.brennaswitzer.cookbook.sudoku;

public interface Solver {

    boolean isSolved();

    String getBoard();

    long getElapsed();

    int getFrameCount();

}
