package com.brennaswitzer.cookbook.domain.redux;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
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
                .collect(Collectors.groupingBy(Ref::getItem))
                .forEach((i, ps) -> System.out.println(new Purchase(ps.stream()
                        .map(Ref::getQuantity)
                        .reduce(Quantity.ZERO, Quantity::plus), i)));
        System.out.println("======================================================================");
    }

}

class GroceryItem implements IngredientLike {
    String name; // orange juice

    public GroceryItem(String name) {
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

interface IngredientLike {
    String getName();
}

class Ingredient implements Ref<IngredientLike> {
    Quantity quantity;
    IngredientLike item;

    public Ingredient(Quantity quantity, IngredientLike item) {
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

class Recipe implements RecipeLike, Shoppable, IngredientLike {
    String name; // enchiladas
    List<Ingredient> ingredients = new ArrayList<>();
    String directions;

    public Recipe(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ItemToPurchase> getShoppingList() {
        ArrayList<ItemToPurchase> result = new ArrayList<>();
        ingredients.forEach(i -> {
            if (i.item instanceof GroceryItem) {
                result.add(new Purchase(i.quantity, (GroceryItem) i.item));
            } else if (i.item instanceof Shoppable) {
                ((Shoppable) i.item).getShoppingList()
                        .stream()
                        .map(it ->
                                new Purchase(i.getQuantity().times(it.getQuantity()), it.getItem()))
                        .forEach(result::add);
            } else {
                throw new IllegalArgumentException("Unknown ingredient type: " + i.item.getClass().getSimpleName());
            }
        });
        return result;
    }

    public Recipe withIngredient(Quantity q, IngredientLike item) {
        ingredients.add(new Ingredient(q, item));
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(name).append('\n');
        ingredients.forEach(i ->
                sb.append(i).append('\n'));
        return sb.toString();
    }

}

class Dish implements Ref<RecipeLike>, Shoppable {
    Quantity quantity;
    RecipeLike recipe;

    public Dish(Quantity quantity, RecipeLike recipe) {
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

    @Override
    public List<ItemToPurchase> getShoppingList() {
        return recipe.getShoppingList()
                .stream()
                .map(i ->
                        new Purchase(i.getQuantity().times(quantity), i.getItem()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return quantity + " " + recipe;
    }

}

class Meal implements RecipeLike, Shoppable {

    String name; // 4th of July BBQ
    List<Dish> dishes = new ArrayList<>();
    List<ItemToPurchase> extraItems = new ArrayList<>();
    String notes;

    public Meal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ItemToPurchase> getShoppingList() {
        List<ItemToPurchase> result = new LinkedList<>(extraItems);
        dishes.forEach(d ->
                result.addAll(d.getShoppingList()));
        return result;
    }

    public Meal withDish(Quantity q, Recipe r) {
        dishes.add(new Dish(q, r));
        return this;
    }

    public Meal withExtraItem(Quantity q, GroceryItem item) {
        extraItems.add(new Purchase(q, item));
        return this;
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

interface RecipeLike extends Shoppable {
    String getName();
}

interface Shoppable {
    List<ItemToPurchase> getShoppingList();
}

interface ItemToPurchase extends Ref<GroceryItem> {
}

class Purchase implements ItemToPurchase {
    Quantity quantity;
    GroceryItem item;

    public Purchase(Quantity quantity, GroceryItem item) {
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
    String name; // week of July 20
    List<Planner> sections = new ArrayList<>();
    List<Dish> dishes = new ArrayList<>();
    List<ItemToPurchase> extraItems = new ArrayList<>();

    public Planner(String name) {
        this.name = name;
    }

    public Planner withSection(String name, Consumer<Planner> sectionWork) {
        Planner section = new Planner(name);
        sections.add(section);
        sectionWork.accept(section);
        return this;
    }

    public Planner withDish(Quantity q, RecipeLike r) {
        dishes.add(new Dish(q, r));
        return this;
    }

    public Planner withExtraItem(Quantity q, GroceryItem item) {
        extraItems.add(new Purchase(q, item));
        return this;
    }

    @Override
    public List<ItemToPurchase> getShoppingList() {
        List<ItemToPurchase> result = new LinkedList<>(extraItems);
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
    Set<String> aliases;
    Map<UnitOfMeasure, Double> conversions;

    public UnitOfMeasure(String name) {
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

class Quantity {
    public static final Quantity ZERO = new Quantity(0, null);
    public static final Quantity ONE = new Quantity(1, null);

    protected static final Log logger = LogFactory.getLog(Quantity.class);

    double amount;
    UnitOfMeasure unit;

    public Quantity(double amount, UnitOfMeasure unit) {
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
        if (!unit.equals(other.unit)) {
            logger.warn("Multiplying Quantities w/ different units?! '" + this + "'.times('" + other + "')");
        }
        return new Quantity(amount * other.amount, unit);
    }

}

class Count extends Quantity {
    public Count(double number) {
        super(number, null);
    }
}

interface Ref<T> {
    Quantity getQuantity();

    T getItem();
}

