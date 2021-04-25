package com.brennaswitzer.cookbook.sudoku.ac3;

public class UnsolvableConstraintsException extends RuntimeException {

    public UnsolvableConstraintsException(int variable) {
        super("Constraints over variable " + variable + " are unsolvable");
    }

}
