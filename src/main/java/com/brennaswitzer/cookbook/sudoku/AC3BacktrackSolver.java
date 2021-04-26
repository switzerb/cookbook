package com.brennaswitzer.cookbook.sudoku;

import com.brennaswitzer.cookbook.sudoku.ac3.BitSetAC3;

public class AC3BacktrackSolver extends DomainBacktrackSolver {

    AC3BacktrackSolver(String board) {
        super(board);
    }

    @Override
    protected void buildDomains() {
        BitSetAC3 ac3 = new BitSetAC3(
                AC3Utils.buildDomains(this),
                AC3Utils.buildUnaryConstraints(this),
                AC3Utils.buildBinaryConstraints(this)
        );
        domains = ac3.getDomains();
    }

}
