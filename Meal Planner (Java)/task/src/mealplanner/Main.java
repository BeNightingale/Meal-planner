package mealplanner;

import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        String choice = Order.forceToMakeChoice();
        while (!Objects.equals(choice, "exit")) {
            Order.actionMap.get(choice).run();
            choice = Order.forceToMakeChoice();
        }
        Order.actionMap.get(choice).run();
    }
}