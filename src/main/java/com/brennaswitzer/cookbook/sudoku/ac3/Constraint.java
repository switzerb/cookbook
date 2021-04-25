package com.brennaswitzer.cookbook.sudoku.ac3;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.function.Predicate;

@Value
public class Constraint {

    protected int x;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected Predicate<Integer> constraint;

}
