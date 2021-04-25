package com.brennaswitzer.cookbook.sudoku;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.BitSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    @Value
    private static class Constraint {

        int x;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        Predicate<Integer> constraint;

    }

    @Value
    private static class BiConstraint {

        int x, y;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        BiPredicate<Integer, Integer> constraint;

    }

    private BitSet[] domains;

    protected boolean solve() {
        // variables are implicit: [0-len)
        domains = buildDomains();
        for (Constraint c : buildUnaryConstraints()) {
            constrainDomain(domains[c.x], c.constraint);
        }
        //noinspection unchecked
        Bag<BiConstraint>[] inboundArcs = (Bag<BiConstraint>[]) new Bag[len];
        for (int c = 0; c < len; c++) {
            Bag<BiConstraint> arcs = new Bag<>();
            inboundArcs[c] = arcs;
        }
        for (BiConstraint c : buildBinaryConstraints()) {
            inboundArcs[c.y].push(c);
        }

        UniqueBag<BiConstraint> queue = new UniqueBag<>();
        for (Bag<BiConstraint> arcs : inboundArcs) {
            for (BiConstraint a : arcs) {
                queue.push(a);
            }
        }
        while (!queue.isEmpty()) {
            enterFrame();
            BiConstraint arc = queue.pop();
            if (arcReduce(arc)) {
                if (domains[arc.x].isEmpty()) {
                    throw new IllegalArgumentException("Inconsistent/unsolvable problem?!");
                }
                for (BiConstraint a : inboundArcs[arc.x]) {
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

    private boolean arcReduce(BiConstraint arc) {
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

    private Bag<BiConstraint> buildBinaryConstraints() {
        Bag<BiConstraint> cons = new Bag<>();
        for (int c = 0; c < len; c++) {
            for (int n : getNeighbors(c)) {
                cons.push(new BiConstraint(c, n, (a, b) -> !a.equals(b)));
            }
        }
        return cons;
    }

    private Bag<Constraint> buildUnaryConstraints() {
        Bag<Constraint> cons = new Bag<>();
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
