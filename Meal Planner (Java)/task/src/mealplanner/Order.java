package mealplanner;

import java.util.*;

import static java.util.Map.entry;

public class Order {

    private static final String SPLITER = ", ";
    private static final String COMMAND_USE_LETTERS = "Wrong format. Use letters only!";

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Meal> mealsList = new ArrayList<>();
    private static final Runnable add = Order::addMeal;
    private static final Runnable show = Order::showMeals;
    private static final Runnable exit = Order::exit;
    protected static final Map<String, Runnable> actionMap = Map.ofEntries(
            entry("add", add),
            entry("show", show),
            entry("exit", exit));

    private Order() {
        // default
    }

    public static String forceToMakeChoice() {
        String choice = Order.actionChoice();
        while (!isChoiceValid(choice))
            choice = Order.actionChoice();
        return choice;
    }

    private static String actionChoice() {
        System.out.println("What would you like to do (add, show, exit)?");
        return scanner.nextLine();
    }

    private static String specifyMeal() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String mealCategory = scanner.nextLine();
        while (!isMealCategoryValid(mealCategory)) {
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            mealCategory = scanner.nextLine();
        }
        return mealCategory;
    }

    private static String getMealName() {
        System.out.println("Input the meal's name:");
        String mealName = scanner.nextLine();
        while (isInputNotAWord(mealName)) {
            System.out.println(COMMAND_USE_LETTERS);
            mealName = scanner.nextLine();
        }
        return mealName;
    }

    private static List<String> getIngredients() {
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

    private static void addMeal() {
        mealsList.add(new Meal(
                        Order.specifyMeal(),
                        Order.getMealName(),
                        Order.getIngredients()
                )
        );
    }

    private static void showMeals() {
        if (mealsList.isEmpty())
            System.out.println("No meals saved. Add a meal first.");
        for (Meal meal : mealsList) {
            System.out.println(meal);
        }
    }

    private static void exit() {
        System.out.println("Bye!");
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
        String regex = "[a-zA-Z ]+";
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
