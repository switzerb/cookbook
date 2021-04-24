package com.brennaswitzer.cookbook.sudoku;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UniqueBag<E> implements Iterable<E> {

    private final Bag<E> bag = new Bag<>();
    private final Set<E> set = new HashSet<>();

    public boolean push(E e) {
        if (!set.add(e)) return false;
        bag.push(e);
        return true;
    }

    public E pop() {
        E e = bag.pop();
        if (!set.remove(e)) throw new IllegalStateException();
        return e;
    }

    // delegate methods

    public boolean isEmpty() {
        return bag.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return bag.iterator();
    }

    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return set.contains(o);
    }

}
