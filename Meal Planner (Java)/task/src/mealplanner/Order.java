package mealplanner;

import java.util.*;

public class Order {

    private static final String SPLITER = ", ";
    private static final String COMMAND_USE_LETTERS = "Wrong format. Use letters only!";
    private static final Scanner scanner = new Scanner(System.in);

    protected Order() {
        // default
    }

    public static String forceToMakeChoice() {
        String choice = actionChoice();
        while (!isChoiceValid(choice))
            choice = actionChoice();
        return choice;
    }

    protected static String specifyMeal() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String mealCategory = scanner.nextLine();
        while (!isMealCategoryValid(mealCategory)) {
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            mealCategory = scanner.nextLine();
        }
        return mealCategory;
    }

    protected static String getMealName() {
        System.out.println("Input the meal's name:");
        String mealName = scanner.nextLine();
        while (isInputNotAWord(mealName)) {
            System.out.println(COMMAND_USE_LETTERS);
            mealName = scanner.nextLine();
        }
        return mealName;
    }

    protected static List<String> getIngredients() {
        System.out.println("Input the ingredients:");
        String ingredientsString = scanner.nextLine();
        while (isIngredientsStringEmptyOrWithSplitRegexEnd(ingredientsString)) {
            System.out.println(COMMAND_USE_LETTERS);
            ingredientsString = scanner.nextLine();
        }
        String[] ingredients = ingredientsString.split(SPLITER);
        while (areIngredientsNotValid(ingredients)) {
            System.out.println(COMMAND_USE_LETTERS);
            ingredientsString = scanner.nextLine();
            while (isIngredientsStringEmptyOrWithSplitRegexEnd(ingredientsString)) {
                System.out.println(COMMAND_USE_LETTERS);
                ingredientsString = scanner.nextLine();
            }
            ingredients = ingredientsString.split(SPLITER);
        }
        System.out.println("The meal has been added!");
        return Arrays.stream(ingredients).toList();
    }

    private static String actionChoice() {
        System.out.println("What would you like to do (add, show, exit)?");
        return scanner.nextLine();
    }

    // VALIDATION METHODS

    private static boolean isChoiceValid(String choice) {
        final List<String> possibleChoices = Arrays.asList("add", "show", "exit");
        return possibleChoices.contains(choice);
    }

    private static boolean isMealCategoryValid(String mealCategory) {
        final List<String> possibleMealCategories = Arrays.asList("breakfast", "lunch", "dinner");
        return possibleMealCategories.contains(mealCategory);
    }

    private static boolean isInputNotAWord(String input) {
        final String regex = "[a-zA-Z ]+";
        return input == null || !input.matches(regex);
    }

    private static boolean isIngredientsStringEmptyOrWithSplitRegexEnd(String ingredientsString) {
        return ingredientsString == null || ingredientsString.isEmpty() || ingredientsString.endsWith(", ");
    }

    private static boolean areIngredientsNotValid(String[] ingredients) {
        return Arrays.stream(ingredients).anyMatch(Order::isInputNotAWord) ||
                Arrays.stream(ingredients).anyMatch(ingredient -> ingredient == null || ingredient.isEmpty());
    }
}
