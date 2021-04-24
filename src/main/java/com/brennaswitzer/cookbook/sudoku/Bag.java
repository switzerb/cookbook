package com.brennaswitzer.cookbook.sudoku;

import lombok.Value;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bag <E> implements Iterable<E> {

    @Value
    private static class Node<E> {
        E data;
        Node<E> next;
    }

    private Node<E> head;

    public boolean isEmpty() {
        return head == null;
    }

    public void push(E value) {
        head = new Node<>(value, head);
    }

    public E pop() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        E d = head.data;
        head = head.next;
        return d;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Node<E> next = head;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                E d = next.data;
                next = next.next;
                return d;
            }
        };
    }

}
