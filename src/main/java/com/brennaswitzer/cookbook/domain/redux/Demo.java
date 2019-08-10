package com.brennaswitzer.cookbook.domain.redux;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Demo {

    public static void main(String[] args) {
        UnitOfMeasure cup = new UnitOfMeasure("cup");

        GroceryItem egg = new GroceryItem("egg");
        GroceryItem milk = new GroceryItem("milk");
        GroceryItem onion = new GroceryItem("onion");
        GroceryItem oj = new GroceryItem("OJ");

        Recipe scrambledEggs = new Recipe("Scrambled Eggs")
                .withIngredient(new Count(2), egg)
                .withIngredient(new Quantity(0.125, cup), milk);
        Recipe onionOmelet = new Recipe("Onion Omelet")
                // this one is admittedly sorta silly. :)
                .withIngredient(new Count(2), scrambledEggs)
                .withIngredient(new Quantity(0.25, cup), onion);

        Meal breakfast = new Meal("Breakfast")
                .withDish(new Count(2), onionOmelet)
                .withExtraItem(new Quantity(1, cup), oj);

        Plan thisWeek = new Plan("This Week")
                .withSection("Monday", s -> s
                        .withDish(Quantity.ONE, breakfast))
                .withSection("Tuesday", s -> s
                        .withDish(new Count(4), scrambledEggs)
                        .withExtraItem(new Quantity(1, cup), oj)
                )
                .withExtraItem(new Quantity(1, cup), milk);

        System.out.println("======================================================================");
        System.out.println(thisWeek);
        System.out.println("======================================================================");
        thisWeek.getGroceryList().stream()
                .collect(Collectors.groupingBy(Element::getItem))
                .forEach((i, ps) -> System.out.println(new RequiredItem(ps.stream()
                        .map(Element::getQuantity)
                        .reduce(Quantity.ZERO, Quantity::plus), i)));
        System.out.println("======================================================================");
    }

}

interface Named {
    String getName();
}

interface ItemInRecipe extends Named {
}

interface ItemInPlan extends Named, RequiresGroceries {
}

interface RequiresGroceries {
    List<Element<GroceryItem>> getGroceryList();
}

// implementations MUST provide a public (Quantity, T) constructor
interface Element<T extends Named> {
    Quantity getQuantity();

    T getItem();

    default Element<T> scale(double amount) {
        return scale(new Count(amount));
    }

    default Element<T> scale(Quantity quantity) {
        try {
            //noinspection unchecked
            return this.getClass()
                    .getConstructor(
                            Quantity.class,
                            getItem().getClass())
                    .newInstance(
                            getQuantity().times(quantity),
                            getItem());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}

class GroceryItem implements ItemInRecipe {
    private String name; // orange juice

    GroceryItem(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

}

class RequiredItem implements Element<GroceryItem> {
    private Quantity quantity;
    private GroceryItem item;

    @SuppressWarnings("WeakerAccess")
    public RequiredItem(Quantity quantity, GroceryItem item) {
        this.quantity = quantity;
        this.item = item;
    }

    @Override
    public Quantity getQuantity() {
        return quantity;
    }

    @Override
    public GroceryItem getItem() {
        return item;
    }

    @Override
    public String toString() {
        return quantity + " " + item;
    }
}

class Recipe implements ItemInPlan, RequiresGroceries, ItemInRecipe {

    static class Ref implements Element<ItemInRecipe> {
        Quantity quantity;
        ItemInRecipe item;

        @SuppressWarnings("WeakerAccess")
        public Ref(Quantity quantity, ItemInRecipe item) {
            this.quantity = quantity;
            this.item = item;
        }

        @Override
        public Quantity getQuantity() {
            return quantity;
        }

        @Override
        public ItemInRecipe getItem() {
            return item;
        }

        @Override
        public String toString() {
            return quantity + " " + item;
        }

    }

    private String name; // enchiladas
    private List<Ref> ingredients = new ArrayList<>();
    private String directions;

    Recipe(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Element<GroceryItem>> getGroceryList() {
        ArrayList<Element<GroceryItem>> result = new ArrayList<>();
        ingredients.forEach(i -> {
            if (i.item instanceof GroceryItem) {
                result.add(new RequiredItem(i.quantity, (GroceryItem) i.item));
            } else if (i.item instanceof RequiresGroceries) {
                ((RequiresGroceries) i.item).getGroceryList()
                        .stream()
                        .map(it -> it.scale(i.getQuantity()))
                        .forEach(result::add);
            } else {
                throw new IllegalArgumentException("Unknown ingredient type: " + i.item.getClass().getSimpleName());
            }
        });
        return result;
    }

    Recipe withIngredient(Quantity q, ItemInRecipe item) {
        ingredients.add(new Ref(q, item));
        return this;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(name).append('\n');
        ingredients.forEach(i ->
                sb.append(i).append('\n'));
        return sb.toString();
    }

}

class Meal implements ItemInPlan, RequiresGroceries {

    static class Ref implements Element<ItemInPlan> {
        Quantity quantity;
        ItemInPlan recipe;

        @SuppressWarnings("WeakerAccess")
        public Ref(Quantity quantity, ItemInPlan recipe) {
            this.quantity = quantity;
            this.recipe = recipe;
        }

        @Override
        public Quantity getQuantity() {
            return quantity;
        }

        @Override
        public ItemInPlan getItem() {
            return recipe;
        }

        List<Element<GroceryItem>> getShoppingList() {
            return recipe.getGroceryList()
                    .stream()
                    .map(it -> it.scale(quantity))
                    .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return quantity + " " + recipe;
        }

    }

    private String name; // 4th of July BBQ
    private List<Ref> dishes = new ArrayList<>();
    private List<RequiredItem> extraItems = new ArrayList<>();
    private String notes;

    Meal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Element<GroceryItem>> getGroceryList() {
        List<Element<GroceryItem>> result = new LinkedList<>(extraItems);
        dishes.forEach(d ->
                result.addAll(d.getShoppingList()));
        return result;
    }

    Meal withDish(Quantity q, Recipe r) {
        dishes.add(new Ref(q, r));
        return this;
    }

    Meal withExtraItem(Quantity q, GroceryItem item) {
        extraItems.add(new RequiredItem(q, item));
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(name).append('\n');
        dishes.forEach(d ->
                sb.append(d).append('\n'));
        extraItems.forEach(i ->
                sb.append(i).append('\n'));
        return sb.toString();
    }

}

class Plan implements RequiresGroceries {

    static class Ref implements Element<ItemInPlan> {
        Quantity quantity;
        ItemInPlan recipe;

        @SuppressWarnings("WeakerAccess")
        public Ref(Quantity quantity, ItemInPlan recipe) {
            this.quantity = quantity;
            this.recipe = recipe;
        }

        @Override
        public Quantity getQuantity() {
            return quantity;
        }

        @Override
        public ItemInPlan getItem() {
            return recipe;
        }

        List<Element<GroceryItem>> getShoppingList() {
            return recipe.getGroceryList()
                    .stream()
                    .map(it -> it.scale(quantity))
                    .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return quantity + " " + recipe;
        }

    }

    private String name; // week of July 20
    private List<Plan> sections = new ArrayList<>();
    private List<Ref> dishes = new ArrayList<>();
    private List<RequiredItem> extraItems = new ArrayList<>();

    Plan(String name) {
        this.name = name;
    }

    Plan withSection(String name, Consumer<Plan> sectionWork) {
        Plan section = new Plan(name);
        sections.add(section);
        sectionWork.accept(section);
        return this;
    }

    Plan withDish(Quantity q, ItemInPlan r) {
        dishes.add(new Ref(q, r));
        return this;
    }

    Plan withExtraItem(Quantity q, GroceryItem item) {
        extraItems.add(new RequiredItem(q, item));
        return this;
    }

    @Override
    public List<Element<GroceryItem>> getGroceryList() {
        List<Element<GroceryItem>> result = new LinkedList<>(extraItems);
        sections.forEach(s ->
                result.addAll(s.getGroceryList()));
        dishes.forEach(d ->
                result.addAll(d.getShoppingList()));
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(name).append('\n');
        sections.forEach(s ->
                sb.append(s).append('\n'));
        dishes.forEach(d ->
                sb.append(d).append('\n'));
        extraItems.forEach(i ->
                sb.append(i).append('\n'));
        return sb.toString();
    }
}

class UnitOfMeasure {
    String name;

    UnitOfMeasure(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitOfMeasure)) return false;
        UnitOfMeasure that = (UnitOfMeasure) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

// maybe this triple?
//   interface Quantity
//   class ScaledQuantity implements Quantity
//   class Count implements Quantity

class Quantity {
    public static final Quantity ZERO = new Quantity(0, null);
    public static final Quantity ONE = new Quantity(1, null);

    private static final Log logger = LogFactory.getLog(Quantity.class);

    private double amount;
    private UnitOfMeasure unit;

    Quantity(double amount, UnitOfMeasure unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public String toString() {
        if (unit == null) return "" + amount;
        return amount + " " + unit.name;
    }

    public Quantity plus(Quantity other) {
        if (this == ZERO) return other;
        if (other == ZERO) return this;
        if (unit == null && other.unit == null) {
            return new Count(amount + other.amount);
        }
        if (unit == null || other.unit == null || !unit.equals(other.unit)) {
            logger.warn("Adding Quantities w/ different units?! '" + this + "'.plus('" + other + "')");
        }
        return new Quantity(amount + other.amount, unit);
    }

    public Quantity times(Quantity other) {
        if (this == ZERO || other == ZERO) return ZERO;
        if (this == ONE) return other;
        if (other == ONE) return this;
        if (unit == null && other.unit == null) {
            return new Count(amount * other.amount);
        }
        if (unit == null) {
            return new Quantity(amount * other.amount, other.unit);
        }
        if (other.unit == null) {
            return new Quantity(amount * other.amount, unit);
        }
        logger.warn("Multiplying Quantities w/ units?! '" + this + "'.times('" + other + "')");
        return new Quantity(amount * other.amount, unit);
    }

}

class Count extends Quantity {
    Count(double number) {
        super(number, null);
    }
}

