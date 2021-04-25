package com.brennaswitzer.cookbook.sudoku;

import com.brennaswitzer.cookbook.sudoku.ac3.BiConstraint;
import com.brennaswitzer.cookbook.sudoku.ac3.BitSetAC3;
import com.brennaswitzer.cookbook.sudoku.ac3.Constraint;
import com.brennaswitzer.cookbook.sudoku.util.Bag;
import com.brennaswitzer.cookbook.sudoku.util.LinkedBag;

import java.util.BitSet;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    protected boolean solve() {
        BitSetAC3 ac3 = new BitSetAC3(
                buildDomains(),
                buildUnaryConstraints(),
                buildBinaryConstraints()
        );
        return rebuildBoard(ac3.getDomains());
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

    private Bag<BiConstraint> buildBinaryConstraints() {
        Bag<BiConstraint> cons = new LinkedBag<>();
        for (int c = 0; c < len; c++) {
            for (int n : getNeighbors(c)) {
                cons.push(new BiConstraint(c, n, (a, b) -> !a.equals(b)));
            }
        }
        return cons;
    }

    private Bag<Constraint> buildUnaryConstraints() {
        Bag<Constraint> cons = new LinkedBag<>();
        for (int c = 0; c < len; c++) {
            if (board[c] != EMPTY_CELL) {
                int n = board[c];
                cons.push(new Constraint(c, i -> i.equals(n)));
            }
        }
        return cons;
    }

    private BitSet[] buildDomains() {
        BitSet seed = new BitSet(dim);
        for (int i = 1; i <= dim; i++) {
            seed.set(i);
        }
        BitSet[] domains = new BitSet[len];
        for (int i = 0; i < len; i++) {
            domains[i] = (BitSet) seed.clone();
        }
        return domains;
    }

}
