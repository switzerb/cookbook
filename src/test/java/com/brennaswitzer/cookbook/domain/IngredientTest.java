package com.brennaswitzer.cookbook.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IngredientTest {

    Ingredient i;

    @Before
    public void setUp() throws Exception {
        i = new Ingredient() {};
    }

    @Test
    public void emptyLabelsTest() {
        assertTrue(i.getLabels().isEmpty());
    }

    @Test
    public void addLabelRefTest() {
        Label label = new Label("chicken");
        i.addLabel(label);
        assertTrue(i.getLabels().contains(new LabelRef(label)));
    }

    @Test
    public void removeLabelRefTest() {
        Label label = new Label("chicken");
        i.addLabel(label);
        assertTrue(i.getLabels().contains(new LabelRef(label)));
        i.removeLabel(label);
        assertTrue(i.getLabels().isEmpty());
    }

    @Test
    public void getAllLabelsTest() {
        List<Label> labels = new ArrayList<>();
        labels.add(new Label("chicken"));
        labels.add(new Label("dinner"));
        labels.add(new Label("make-ahead"));

        i.addAllLabels(labels);
        assertEquals(i.getLabels().size(), 3);
    }
}
