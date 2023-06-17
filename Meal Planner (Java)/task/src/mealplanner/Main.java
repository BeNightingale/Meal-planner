package mealplanner;

import java.sql.*;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        final String DB_URL = "jdbc:postgresql:meals_db";
        final String USER = "postgres";
        final String PASS = "1111";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            connection.setAutoCommit(true);
            final MealService mealService = new MealService(connection);
            final Statement statement = connection.createStatement();
//            Possibility of cleaning everything in tables
//            statement.executeUpdate("DROP SEQUENCE IF EXISTS meals_sequence");
//            statement.executeUpdate("DROP TABLE meals");
//            statement.executeUpdate("DROP TABLE ingredients");
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS meals_sequence start 1 increment 1 cache 1");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                    "category VARCHAR(30) NOT NULL, " +
                    "meal VARCHAR(100) NOT NULL, " +
                    "meal_id INTEGER NOT NULL PRIMARY KEY" +
                    ")");

       //     statement.executeUpdate("DROP SEQUENCE IF EXISTS ingredients_sequence");
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS ingredients_sequence start 1 increment 1 cache 1");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "ingredient VARCHAR(100) NOT NULL, " +
                    "ingredient_id INTEGER NOT NULL, " +
                    "meal_id INTEGER NOT NULL" +
                    ")");

            String choice = Order.forceToMakeChoice();
            while (!Objects.equals(choice, "exit")) {
                mealService.actionMap.get(choice).run();
                choice = Order.forceToMakeChoice();
            }
            mealService.actionMap.get(choice).run();
            // Possibility of showing all meals
//          List<Meal> meals = mealService.findMeals();
//          meals.stream().forEach(System.out::println);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}