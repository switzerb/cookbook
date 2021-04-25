package com.brennaswitzer.cookbook.sudoku;

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public final class Utils {

    private Utils() { throw new UnsupportedOperationException("Really?"); }

    public static int[] getNeighbors(Sudoku s, int cell) {
        int[] result = new int[(s.dim - 1) * 2 + (s.boxDim - 1) * (s.boxDim - 1)];
        int idx = 0;

        int row = cell / s.dim;
        int col = cell % s.dim;
        for (int i = 0; i < s.dim; i++) {
            if (i != col) {
                result[idx++] = s.idx(row, i);
            }
            if (i != row) {
                result[idx++] = s.idx(i, col);
            }
        }

        int boxRow = row - (row % s.boxDim);
        int boxCol = col - (col % s.boxDim);
        for (int r = 0; r < s.boxDim; r++) {
            if (boxRow + r == row) continue;
            for (int c = 0; c < s.boxDim; c++) {
                if (boxCol + c == col) continue;
                result[idx++] = s.idx(boxRow + r, boxCol + c);
            }
        }
        return result;
    }

    /**
     * I iterate over the indexes of the set bits in the passed BitSet, in
     * order. Unlike most Iterables, concurrent modification IS NOT checked.
     * Any changes at or before the last returned index will not affect
     * iteration. Changes after the last returned index will result in undefined
     * behavior.
     *
     * @param bitSet The BitSet to iterate over.
     * @return An Iterable decorating the passed BitSet.
     */
    public static Iterable<Integer> asIterable(BitSet bitSet) {
        return () -> new PrimitiveIterator.OfInt() {
            int next = bitSet.nextSetBit(0);

            @Override
            public boolean hasNext() {
                return next != -1;
            }

            @Override
            public int nextInt() {
                if (next != -1) {
                    int ret = next;
                    next = bitSet.nextSetBit(next+1);
                    return ret;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

}
