package com.brennaswitzer.cookbook.sudoku;

import lombok.AllArgsConstructor;

import java.util.BitSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
        applyGivens();
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
                if (domains[arc.x].isEmpty()) {
                    throw new IllegalArgumentException("Inconsistent/unsolvable problem?!");
                }
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
        BitSet Dy = domains[arc.y];
        return constrainDomain(Dx, vx ->
                Dy.stream().anyMatch(vy ->
                        arc.constraint.test(vx, vy)));
    }

    private boolean constrainDomain(BitSet Dx, Predicate<Integer> constraint) {
        boolean changed = false;
        for (int i = Dx.nextSetBit(0); i >= 0; i = Dx.nextSetBit(i + 1)) {
            if (!constraint.test(i)) {
                Dx.clear(i);
                changed = true;
            }
        }
        return changed;
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

    private void applyGivens() {
        for (int c = 0; c < len; c++) {
            if (board[c] != EMPTY_CELL) {
                int n = board[c];
                constrainDomain(domains[c], i -> i.equals(n));
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
