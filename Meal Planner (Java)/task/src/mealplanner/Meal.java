package mealplanner;

import java.util.List;

public class Meal {

    private final Integer id;
    private final String category;
    private final String name;
    private final List<String> ingredientsList;

    public Meal(Integer id, String category, String name, List<String> ingredientsList) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.ingredientsList = ingredientsList;
    }

    public Meal(String category, String name, List<String> ingredientsList) {
        this(null, category, name, ingredientsList);
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredientsList() {
        return ingredientsList;
    }

    public void printMeal() {
        final StringBuilder stringBuilder = new StringBuilder();
        addIngredientsToStringBuilder(stringBuilder, this);
        System.out.println(stringBuilder);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Category: ")
                .append(this.category)
                .append("\n")
                .append("Id: ")
                .append(this.id)
                .append("\n");
        addIngredientsToStringBuilder(stringBuilder, this);
        return stringBuilder.toString();
    }

    private void addIngredientsToStringBuilder(StringBuilder stringBuilder, Meal meal) {
        stringBuilder.append("Name: ")
                .append(meal.name)
                .append("\n")
                .append("Ingredients: \n");
        if (meal.ingredientsList != null) {
            meal.ingredientsList
                    .forEach(ingredient -> stringBuilder.append(ingredient.trim()).append("\n"));
        }
    }
}