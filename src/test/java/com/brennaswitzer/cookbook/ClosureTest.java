package com.brennaswitzer.cookbook;

import org.junit.Test;

import java.util.function.Function;

public class ClosureTest {

    // more concise type alias
    private interface Adder extends Function<Integer, Integer> {}

    private static int plus(int a, int b) {
        return a + b;
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void _1_lambda() {
        int x = 2;
        Adder addX = b -> plus(b, x);
        assert 5 == addX.apply(3);
        // x = 7; // COMPILER ERROR!
        /* If the assignment were allowed, which of these is correct?
            assert 5 == addX.apply(3);
            assert 10 == addX.apply(3); // JavaScript says this one
         */
    }

    ////////////////////////////////////////////////////////////////////////////

    private static Adder buildAdder(int addend) {
        return b -> plus(addend, b);
    }

    @Test
    public void _2_builder() {
        int x = 2;
        Adder addX = buildAdder(x);
        assert 5 == addX.apply(3);
        //noinspection UnusedAssignment
        x = 7; // no-op, since x is passed by value, not reference.
        assert 5 == addX.apply(3);
    }

    ////////////////////////////////////////////////////////////////////////////

    private static class SimpleAdder implements Adder {
        private final int addend;

        public SimpleAdder(int addend) {
            this.addend = addend;
        }

        public Integer apply(Integer b) {
            return plus(addend, b);
        }
    }

    @Test
    public void _3_object() {
        int x = 2;
        Adder addX = new SimpleAdder(x);
        assert 5 == addX.apply(3);
        //noinspection UnusedAssignment
        x = 7; // no-op, since x is passed by value, not reference.
        assert 5 == addX.apply(3);
    }

    ////////////////////////////////////////////////////////////////////////////

    private static class Box<E> {
        private final E value;

        public Box(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }
    }

    private static class BoxAdder implements Adder {
        private final Box<Integer> addendBox;

        private BoxAdder(Box<Integer> addendBox) {
            this.addendBox = addendBox;
        }

        public Integer apply(Integer b) {
            return plus(addendBox.getValue(), b);
        }
    }

    @Test
    public void _4_boxed() {
        Box<Integer> x = new Box<>(2);
        Adder addX = new BoxAdder(x);
        assert 5 == addX.apply(3);
        // x.setValue(7); // COMPILER ERROR!
    }

    ////////////////////////////////////////////////////////////////////////////

    private static class Cell<E> {
        private E value;

        public Cell(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }
    }

    private static class LazyCellAdder implements Adder {
        private final Cell<Integer> addendCell;

        private LazyCellAdder(Cell<Integer> addendCell) {
            this.addendCell = addendCell;
        }

        public Integer apply(Integer b) {
            return plus(addendCell.getValue(), b);
        }
    }

    @Test
    public void _5_lazyCell() {
        Cell<Integer> x = new Cell<>(2);
        Adder addX = new LazyCellAdder(x);
        assert 5 == addX.apply(3);
        x.setValue(7);
        assert 10 == addX.apply(3); // now it's 10!
    }

    ////////////////////////////////////////////////////////////////////////////

    private static class EagerCellAdder implements Adder {
        private final int addend;

        public EagerCellAdder(Cell<Integer> addendCell) {
            addend = addendCell.getValue();
        }

        public Integer apply(Integer b) {
            return plus(addend, b);
        }
    }

    @Test
    public void _6_eagerCell() {
        Cell<Integer> x = new Cell<>(2);
        Adder addX = new EagerCellAdder(x);
        assert 5 == addX.apply(3);
        x.setValue(7);
        assert 5 == addX.apply(3); // it's still 5!
    }

}
