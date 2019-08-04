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

        Planner thisWeek = new Planner("This Week")
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
        thisWeek.getShoppingList().stream()
                .collect(Collectors.groupingBy(AmountOf::getItem))
                .forEach((i, ps) -> System.out.println(new Purchase(ps.stream()
                        .map(AmountOf::getQuantity)
                        .reduce(Quantity.ZERO, Quantity::plus), i)));
        System.out.println("======================================================================");
    }

}

class GroceryItem implements IngredientLike {
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

interface IngredientLike extends Named {
    String getName();
}

class Recipe implements RecipeLike, Shoppable, IngredientLike {

    static class Ref implements AmountOf<IngredientLike> {
        Quantity quantity;
        IngredientLike item;

        Ref(Quantity quantity, IngredientLike item) {
            this.quantity = quantity;
            this.item = item;
        }

        @Override
        public Quantity getQuantity() {
            return quantity;
        }

        @Override
        public IngredientLike getItem() {
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
    public List<AmountOf<GroceryItem>> getShoppingList() {
        ArrayList<AmountOf<GroceryItem>> result = new ArrayList<>();
        ingredients.forEach(i -> {
            if (i.item instanceof GroceryItem) {
                result.add(new Purchase(i.quantity, (GroceryItem) i.item));
            } else if (i.item instanceof Shoppable) {
                ((Shoppable) i.item).getShoppingList()
                        .stream()
                        .map(it -> it.scale(i.getQuantity()))
                        .forEach(result::add);
            } else {
                throw new IllegalArgumentException("Unknown ingredient type: " + i.item.getClass().getSimpleName());
            }
        });
        return result;
    }

    Recipe withIngredient(Quantity q, IngredientLike item) {
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

class Meal implements RecipeLike, Shoppable {

    static class Ref implements AmountOf<RecipeLike> {
        Quantity quantity;
        RecipeLike recipe;

        Ref(Quantity quantity, RecipeLike recipe) {
            this.quantity = quantity;
            this.recipe = recipe;
        }

        @Override
        public Quantity getQuantity() {
            return quantity;
        }

        @Override
        public RecipeLike getItem() {
            return recipe;
        }

        List<AmountOf<GroceryItem>> getShoppingList() {
            return recipe.getShoppingList()
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
    private List<AmountOf<GroceryItem>> extraItems = new ArrayList<>();
    private String notes;

    Meal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<AmountOf<GroceryItem>> getShoppingList() {
        List<AmountOf<GroceryItem>> result = new LinkedList<>(extraItems);
        dishes.forEach(d ->
                result.addAll(d.getShoppingList()));
        return result;
    }

    Meal withDish(Quantity q, Recipe r) {
        dishes.add(new Ref(q, r));
        return this;
    }

    Meal withExtraItem(Quantity q, GroceryItem item) {
        extraItems.add(new Purchase(q, item));
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

interface RecipeLike extends Named, Shoppable {
    String getName();
}

// this is really "requires items" or something?
interface Shoppable {
    List<AmountOf<GroceryItem>> getShoppingList();
}

// this is really "required item" or something?
class Purchase implements AmountOf<GroceryItem> {
    private Quantity quantity;
    private GroceryItem item;

    Purchase(Quantity quantity, GroceryItem item) {
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

class Planner implements Shoppable {

    static class Ref implements AmountOf<RecipeLike> {
        Quantity quantity;
        RecipeLike recipe;

        Ref(Quantity quantity, RecipeLike recipe) {
            this.quantity = quantity;
            this.recipe = recipe;
        }

        @Override
        public Quantity getQuantity() {
            return quantity;
        }

        @Override
        public RecipeLike getItem() {
            return recipe;
        }

        List<AmountOf<GroceryItem>> getShoppingList() {
            return recipe.getShoppingList()
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
    private List<Planner> sections = new ArrayList<>();
    private List<Ref> dishes = new ArrayList<>();
    private List<AmountOf<GroceryItem>> extraItems = new ArrayList<>();

    Planner(String name) {
        this.name = name;
    }

    Planner withSection(String name, Consumer<Planner> sectionWork) {
        Planner section = new Planner(name);
        sections.add(section);
        sectionWork.accept(section);
        return this;
    }

    Planner withDish(Quantity q, RecipeLike r) {
        dishes.add(new Ref(q, r));
        return this;
    }

    Planner withExtraItem(Quantity q, GroceryItem item) {
        extraItems.add(new Purchase(q, item));
        return this;
    }

    @Override
    public List<AmountOf<GroceryItem>> getShoppingList() {
        List<AmountOf<GroceryItem>> result = new LinkedList<>(extraItems);
        sections.forEach(s ->
                result.addAll(s.getShoppingList()));
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

interface Named {
    String getName();
}

interface AmountOf<T extends Named> {
    Quantity getQuantity();

    T getItem();

    default AmountOf<T> scale(double amount) {
        return scale(new Count(amount));
    }

    default AmountOf<T> scale(Quantity quantity) {
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

