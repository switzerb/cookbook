package com.brennaswitzer.cookbook.sudoku;

import com.brennaswitzer.cookbook.sudoku.ac3.BiConstraint;
import com.brennaswitzer.cookbook.sudoku.ac3.BitSetAC3;
import com.brennaswitzer.cookbook.sudoku.ac3.Constraint;
import com.brennaswitzer.cookbook.sudoku.util.Bag;
import com.brennaswitzer.cookbook.sudoku.util.LinkedBag;

import java.util.BitSet;

public final class AC3Utils {

    private AC3Utils() { throw new UnsupportedOperationException("Really?"); }

    public static BitSetAC3 ac3(Sudoku s) {
        return new BitSetAC3(
                AC3Utils.buildDomains(s),
                AC3Utils.buildUnaryConstraints(s),
                AC3Utils.buildBinaryConstraints(s)
        );
    }

    public static BitSet[] buildDomains(Sudoku s) {
        BitSet seed = new BitSet(s.dim);
        for (int i = 1; i <= s.dim; i++) {
            seed.set(i);
        }
        BitSet[] domains = new BitSet[s.len];
        for (int i = 0; i < s.len; i++) {
            domains[i] = (BitSet) seed.clone();
        }
        return domains;
    }

    public static Bag<Constraint> buildUnaryConstraints(Sudoku s) {
        Bag<Constraint> cons = new LinkedBag<>();
        for (int c = 0; c < s.len; c++) {
            if (s.board[c] != Sudoku.EMPTY_CELL) {
                int n = s.board[c];
                cons.push(new Constraint(c, i -> i.equals(n)));
            }
        }
        return cons;
    }

    public static Bag<BiConstraint> buildBinaryConstraints(Sudoku s) {
        Bag<BiConstraint> cons = new LinkedBag<>();
        for (int c = 0; c < s.len; c++) {
            for (int n : Utils.getNeighbors(s, c)) {
                cons.push(new BiConstraint(c, n, (a, b) -> !a.equals(b)));
            }
        }
        return cons;
    }

}
