package com.brennaswitzer.cookbook.sudoku;

import com.brennaswitzer.cookbook.sudoku.ac3.BitSetAC3;

import java.util.Arrays;
import java.util.BitSet;

public class AC3BacktrackSolver extends Sudoku {

    AC3BacktrackSolver(String board) {
        super(board);
    }

    private BitSet[] domains;

    protected boolean solve() {
        BitSetAC3 ac3 = new BitSetAC3(
                AC3Utils.buildDomains(this),
                AC3Utils.buildUnaryConstraints(this),
                AC3Utils.buildBinaryConstraints(this)
        );
        domains = ac3.getDomains();
        solveInternal();
        return AC3Utils.rebuildBoard(this, domains);
    }

    private boolean solveInternal() {
        int i = findEmptyCell();
        if (i < 0) return true;
        enterFrame();
        for (int n : Utils.asIterable(domains[i])) {
            BitSet[] prev = Arrays.copyOf(domains, domains.length);
            if (lockCell(i, n) && solveInternal()) return true;
            domains = prev;
        }
        return false;
    }

    private boolean lockCell(int cell, int val) {
        BitSet d = new BitSet(val);
        d.set(val);
        domains[cell] = d;
        for (int n : getNeighbors(cell)) {
            d = domains[n];
            if (!d.get(val)) continue;
            if (d.cardinality() == 1) return false;
            if (d.cardinality() == 2) {
                int v = d.nextSetBit(0);
                if (v == val) {
                    v = d.nextSetBit(v + 1);
                }
                if (!lockCell(n, v)) return false;
                continue;
            }
            d = (BitSet) d.clone();
            d.clear(val);
            domains[n] = d;
        }
        return true;
    }

    private int findEmptyCell() {
        int bestIdx = -1;
        int smallest = Integer.MAX_VALUE;
        for (int i = 0; i < len; i++) {
            int card = domains[i].cardinality();
            if (card == 1) continue;
            if (card < smallest) {
                bestIdx = i;
                smallest = card;
            }
        }
        return bestIdx;
    }

}
