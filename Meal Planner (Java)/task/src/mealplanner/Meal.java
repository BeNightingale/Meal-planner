package mealplanner;

import java.util.List;

public class Meal {

    private final String category;
    private final String name;
    private final List<String> ingredientsList;

    public Meal(String category, String name, List<String> ingredientsList) {
        this.category = category;
        this.name = name;
        this.ingredientsList = ingredientsList;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Category: ")
                .append(this.category)
                .append("\n")
                .append("Name: ")
                .append(this.name)
                .append("\n")
                .append("Ingredients: \n");
        if (this.ingredientsList != null) {
            this.ingredientsList
                    .forEach(ing -> stringBuilder.append(ing).append("\n"));
        }
        stringBuilder.append("The meal has been added!");
        return stringBuilder.toString();
    }
}
