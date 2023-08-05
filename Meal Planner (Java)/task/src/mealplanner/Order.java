package mealplanner;

import java.util.*;

public class Order {

    private static final String SPLITER = ",";
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
        Arrays.stream(ingredients).forEach(String::trim);
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
        System.out.println("What would you like to do (add, show, plan, save, exit)?");
        return scanner.nextLine();
    }

    protected static String makeCorrectPrintChoice() {
        String category = makePrintChoice("Which category do you want to print (breakfast, lunch, dinner)?");
        while (!isMealCategoryValid(category))
            category = makePrintChoice("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        return category;
    }

    protected static List<String> makeCorrectMealNameChoice(Map<String, Integer> mealNamesMap, String weekDayName, String mealCategoryName) {
        String mealNameChoice;
        final List<String> mealNamesList = new ArrayList<>(mealNamesMap.keySet());
        Collections.sort(mealNamesList);
        if (!mealNamesList.isEmpty()) {
            printMealNamesList(mealNamesList);
            System.out.printf("Choose the %s for %s from the list above:%n", mealCategoryName, weekDayName);
            mealNameChoice = scanner.nextLine();
            while (!isMealNameInMealNamesList(mealNamesList, mealNameChoice)) {
                System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                mealNameChoice = scanner.nextLine();
            }
            final Integer mealChoiceId = mealNamesMap.get(mealNameChoice);

            return List.of(mealNameChoice, mealChoiceId == null ? "-1" : mealChoiceId.toString());
        } else {
            System.out.printf("No meals to choose for %s. ", mealCategoryName);
            return Collections.emptyList();
        }
    }

    protected static String getShoppingListFileName() {
        System.out.println("Input a filename:");
        return scanner.nextLine();
    }

    private static String makePrintChoice(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private static void printMealNamesList(Collection<String> mealNamesList) {
        mealNamesList.forEach(System.out::println);
    }

    protected static String printShoppingList(Map<String, Integer> ingredientsGroupedByEqualValueMap) {
        final StringBuilder stringBuilder = new StringBuilder();
        ingredientsGroupedByEqualValueMap.forEach((key, value) -> {
            stringBuilder.append(key);
            if (value > 1) {
                stringBuilder.append(" x").append(value).append("\n");
            } else {
                stringBuilder.append("\n");
            }
        });
        return stringBuilder.toString();
    }

    // VALIDATION METHODS

    private static boolean isChoiceValid(String choice) {
        final List<String> possibleChoices = Arrays.asList("add", "show", "plan", "save", "exit");
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

    private static boolean isMealNameInMealNamesList(Collection<String> mealNamesList, String mealNameChoice) {
        return mealNamesList != null && !mealNamesList.isEmpty() && mealNamesList.contains(mealNameChoice);
    }
}