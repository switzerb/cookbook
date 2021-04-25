package com.brennaswitzer.cookbook.sudoku;

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

}
