package com.brennaswitzer.cookbook.sudoku.util;

public interface Bag<E> extends Iterable<E> {

    boolean isEmpty();

    boolean push(E e);

    E pop();

}
