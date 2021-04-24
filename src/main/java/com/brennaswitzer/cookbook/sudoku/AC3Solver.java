package com.brennaswitzer.cookbook.sudoku;

import lombok.AllArgsConstructor;

import java.util.BitSet;
import java.util.function.BiPredicate;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    @AllArgsConstructor
    private static class Arc {
        final int x, y;
        final BiPredicate<Integer, Integer> constraint;
    }

    private BitSet[] domains;
    private Bag<Arc>[] inboundArcs;

    protected boolean solve() {
        // variables are implicit: [0-len)
        buildDomains();
        buildGivens();
        buildArcs();
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
            if (Dy.stream().noneMatch(vy -> arc.constraint.test(vx, vy))) {
                Dx.clear(i);
                change = true;
            }
        }
        return change;
    }

    private void buildArcs() {
        //noinspection unchecked
        inboundArcs = (Bag<Arc>[]) new Bag[len];
        for (int c = 0; c < len; c++) {
            Bag<Arc> arcs = new Bag<>();
            inboundArcs[c] = arcs;
            for (int n : getNeighbors(c)) {
                arcs.push(new Arc(n, c, (a, b) -> !a.equals(b)));
            }
        }
    }

    private void buildGivens() {
        for (int i = 0; i < len; i++) {
            if (board[i] != EMPTY_CELL) {
                domains[i].clear();
                domains[i].set(board[i]);
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
            domains[i] = (BitSet) seed.clone();
        }
    }

}
