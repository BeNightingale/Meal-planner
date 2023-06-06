package mealplanner;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Order {

    static final Scanner scanner = new Scanner(System.in);

    private Order() {
        // default
    }

    public static String specifyMeal() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        return scanner.nextLine();
    }

    public static String getMealName() {
        System.out.println("Input the meal's name:");
        return scanner.nextLine();
    }

    public static List<String> getIngredients() {
        System.out.println("Input the ingredients:");
        final String[] ingredients = scanner.nextLine().split(",");
        return Arrays.stream(ingredients).toList();
    }

    public static void describeMeal() {
        System.out.println(
                new Meal(
                        Order.specifyMeal(),
                        Order.getMealName(),
                        Order.getIngredients()
                )
        );
    }
}
