package com.brennaswitzer.cookbook.sudoku;

import java.util.BitSet;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    protected boolean solve() {
        BitSet[] domains = AC3Utils.ac3(this).getDomains();
        return AC3Utils.rebuildBoard(this, domains);
    }

}
