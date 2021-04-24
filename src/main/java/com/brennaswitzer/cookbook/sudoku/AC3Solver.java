package com.brennaswitzer.cookbook.sudoku;

import lombok.Value;

import java.util.BitSet;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    @Value
    private static class Arc {
        int x, y;
    }

    private BitSet[] domains;
    private Bag<Arc>[] inboundArcs;

    protected boolean solve() {
        // variables are implicit: [0-len)
        buildDomains();
        buildArcs(); // constraints are all "binary !="
        Bag<Arc> queue = new Bag<>();
        for (Bag<Arc> arcs : inboundArcs) {
            for (Arc a : arcs) {
                queue.push(a);
            }
        }
        while (!queue.isEmpty()) {
            enterFrame();
            Arc arc = queue.pop();
            if (arcReduce(arc)) {
                for (Arc a : inboundArcs[arc.x]) {
                    if (a.x != arc.y) {
                        queue.push(a);
                    }
                }
            }
        }
        return rebuildBoard();
    }

    private boolean rebuildBoard() {
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

    private boolean arcReduce(Arc arc) {
        BitSet Dx = domains[arc.x];
        if (Dx.cardinality() == 1) return false;
        boolean change = false;
        BitSet Dy = domains[arc.y];
        for (int i = Dx.nextSetBit(0); i >= 0; i = Dx.nextSetBit(i + 1)) {
            int vx = i;
            if (Dy.stream().noneMatch(vy -> vx != vy)) {
                Dx.clear(i);
                change = true;
            }
        }
        return change;
    }

    private void buildArcs() {
        //noinspection unchecked
        inboundArcs = (Bag<Arc>[]) new Bag[len];
        int idx = 0;
        for (int c = 0; c < len; c++) {
            Bag<Arc> b = new Bag<>();
            inboundArcs[c] = b;
            for (int n : getNeighbors(c)) {
                b.push(new Arc(n, c));
            }
        }
    }

    private void buildDomains() {
        BitSet seed = new BitSet(dim);
        for (int i = 1; i <= dim; i++) {
            seed.set(i);
        }
        domains = new BitSet[len];
        for (int i = 0; i < len; i++) {
            if (board[i] == EMPTY_CELL) {
                domains[i] = (BitSet) seed.clone();
            } else {
                domains[i] = new BitSet();
                domains[i].set(board[i]);
            }
        }
    }

}
