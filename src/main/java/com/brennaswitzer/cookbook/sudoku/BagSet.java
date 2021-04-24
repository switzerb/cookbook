package com.brennaswitzer.cookbook.sudoku;

import java.util.HashSet;
import java.util.Set;

public class BagSet<E> extends Bag<E> {

    private final Set<E> set = new HashSet<>();

    public void push(E e) {
        if (set.add(e)) super.push(e);
    }

    public E pop() {
        E e = super.pop();
        if (!set.remove(e)) throw new IllegalStateException();
        return e;
    }

}
