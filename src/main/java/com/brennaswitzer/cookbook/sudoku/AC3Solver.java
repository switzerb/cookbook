package com.brennaswitzer.cookbook.sudoku;

import java.util.BitSet;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    protected boolean solve() {
        return rebuildBoard(AC3Utils.ac3(this).getDomains());
    }

    private boolean rebuildBoard(BitSet[] domains) {
        boolean solved = true;
        for (int i = 0; i < len; i++) {
            BitSet d = domains[i];
            if (d.cardinality() == 1) {
                board[i] = d.nextSetBit(0);
            } else {
                solved = false;
            }
        }
        return solved;
    }

}
