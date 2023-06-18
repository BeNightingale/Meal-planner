package mealplanner;

public enum MealCategory {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner");

    private final String categoryName;

    MealCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
