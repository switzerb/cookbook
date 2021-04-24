package com.brennaswitzer.cookbook.sudoku;

import lombok.Value;

import java.util.*;

public class AC3Solver extends Sudoku {

    AC3Solver(String board) {
        super(board);
    }

    @Value
    private static class Arc {
        int x, y;
    }

    private List<Set<Integer>> domains;
    private Set<Arc> arcs;

    protected boolean solve() {
        // variables are implicit: [0-len)
        buildDomains();
        buildArcs(); // constraints are all binary and all "!="
        Queue<Arc> worklist = new LinkedList<>(arcs);
        while (!worklist.isEmpty()) {
            enterFrame();
            Arc arc = worklist.remove();
            if (arcReduce(arc)) {
                for (Arc a : arcs) {
                    if (a.x != arc.y && a.y == arc.x) {
                        worklist.add(a);
                    }
                }
            }
        }
        return rebuildBoard();
    }

    private boolean rebuildBoard() {
        boolean solved = true;
        for (int i = 0; i < domains.size(); i++) {
            Set<Integer> d = domains.get(i);
            if (d.size() == 1) {
                board[i] = d.iterator().next();
            } else {
                solved = false;
            }
        }
        return solved;
    }

    private boolean arcReduce(Arc arc) {
        Set<Integer> Dx = domains.get(arc.x);
        if (Dx.size() == 1) return false;
        boolean change = false;
        Set<Integer> Dy = domains.get(arc.y);
        Iterator<Integer> vxItr = Dx.iterator();
        while (vxItr.hasNext()) {
            int vx = vxItr.next();
            if (Dy.stream().noneMatch(vy -> vx != vy)) {
                vxItr.remove();
                change = true;
            }
        }
        return change;
    }

    private void buildArcs() {
        arcs = new HashSet<>();
        for (int c = 0; c < len; c++) {
            for (int n : getNeighbors(c)) {
                arcs.add(new Arc(c, n));
            }
        }
    }

    private void buildDomains() {
        Collection<Integer> seed = new ArrayList<>(dim);
        for (int i = 1; i <= dim; i++) {
            seed.add(i);
        }
        domains = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            if (board[i] == EMPTY_CELL) {
                domains.add(new HashSet<>(seed));
            } else {
                domains.add(Collections.singleton(board[i]));
            }
        }
    }

}
