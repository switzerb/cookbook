package com.brennaswitzer.cookbook.sudoku;

import java.util.Arrays;
import java.util.BitSet;

public class DomainBacktrackSolver extends Sudoku {

    public DomainBacktrackSolver(String board) {
        super(board);
    }

    protected BitSet[] domains;

    @Override
    protected boolean solve() {
        buildDomains();
        solveInternal();
        return AC3Utils.rebuildBoard(this, domains);
    }

    protected void buildDomains() {
        domains = AC3Utils.buildDomains(this);
        for (int c = 0; c < len; c++) {
            if (board[c] != Sudoku.EMPTY_CELL) {
                lockCell(c, board[c]);
            }
        }
    }

    private boolean solveInternal() {
        int i = findUnknownCell();
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

    private int findUnknownCell() {
        int idx = -1;
        int smallest = Integer.MAX_VALUE;
        for (int i = 0; i < len; i++) {
            int card = domains[i].cardinality();
            if (card == 1) continue;
            if (card < smallest) {
                idx = i;
                smallest = card;
            }
        }
        return idx;
    }

}
