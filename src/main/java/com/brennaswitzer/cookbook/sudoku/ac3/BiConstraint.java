package com.brennaswitzer.cookbook.sudoku.ac3;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.function.BiPredicate;

@Value
public class BiConstraint {

    protected int x, y;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected BiPredicate<Integer, Integer> constraint;

}
