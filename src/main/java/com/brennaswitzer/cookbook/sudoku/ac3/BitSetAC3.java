package com.brennaswitzer.cookbook.sudoku.ac3;

import com.brennaswitzer.cookbook.sudoku.util.Bag;
import com.brennaswitzer.cookbook.sudoku.util.LinkedBag;
import com.brennaswitzer.cookbook.sudoku.util.UniqueLinkedBag;
import lombok.Getter;

import java.util.BitSet;
import java.util.function.Predicate;

public class BitSetAC3 {

    @Getter
    private final BitSet[] domains;

    public BitSetAC3(
            // variables are implicit (in domain count)
            BitSet[] domains,
            Bag<Constraint> unaryConstraints,
            Bag<BiConstraint> binaryConstraints
    ) {
        this.domains = domains;
        for (Constraint c : unaryConstraints) {
            constrainDomain(domains[c.x], c.constraint);
        }
        //noinspection unchecked
        Bag<BiConstraint>[] inboundArcs = (Bag<BiConstraint>[]) new LinkedBag[domains.length];
        for (int c = 0; c < domains.length; c++) {
            Bag<BiConstraint> arcs = new LinkedBag<>();
            inboundArcs[c] = arcs;
        }
        for (BiConstraint c : binaryConstraints) {
            inboundArcs[c.y].push(c);
        }

        Bag<BiConstraint> queue = new UniqueLinkedBag<>();
        for (Bag<BiConstraint> arcs : inboundArcs) {
            for (BiConstraint a : arcs) {
                queue.push(a);
            }
        }
        while (!queue.isEmpty()) {
            BiConstraint arc = queue.pop();
            if (arcReduce(domains, arc)) {
                for (BiConstraint a : inboundArcs[arc.x]) {
                    if (a.x != arc.y) {
                        queue.push(a);
                    }
                }
            }
        }
    }

    private boolean arcReduce(BitSet[] domains, BiConstraint arc) {
        BitSet Dx = domains[arc.x];
        BitSet Dy = domains[arc.y];
        boolean changed = constrainDomain(Dx, vx ->
                Dy.stream().anyMatch(vy ->
                        arc.constraint.test(vx, vy)));
        if (Dx.isEmpty()) {
            throw new UnsolvableConstraintsException(arc.x);
        }
        return changed;
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

}
