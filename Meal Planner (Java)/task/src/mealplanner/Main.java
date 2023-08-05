package mealplanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        final String DB_URL = "jdbc:postgresql:meals_db";
        final String USER = "postgres";
        final String PASS = "1111";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(true);
            final MealService mealService = new MealService(connection);
            final Repository repository = new Repository(connection);
//            Possibility of cleaning everything in tables
//            statement.executeUpdate("DROP SEQUENCE IF EXISTS meals_sequence");
//            statement.executeUpdate("DROP TABLE meals");
//            statement.executeUpdate("DROP TABLE ingredients");
//            statement.executeUpdate("DROP TABLE plan");
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS meals_sequence start 1 increment 1 cache 1");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                    "category VARCHAR(30) NOT NULL, " +
                    "meal VARCHAR(100) NOT NULL, " +
                    "meal_id INTEGER NOT NULL PRIMARY KEY" +
                    ")");

//            statement.executeUpdate("DROP SEQUENCE IF EXISTS ingredients_sequence");
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS ingredients_sequence start 1 increment 1 cache 1");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "ingredient VARCHAR(100) NOT NULL, " +
                    "ingredient_id INTEGER NOT NULL PRIMARY KEY, " +
                    "meal_id INTEGER NOT NULL" +
                    ")");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                    "meal_option VARCHAR(100), " +
                    "meal_category VARCHAR(30) NOT NULL, " +
                    "meal_id INTEGER NOT NULL, " +
                    "day VARCHAR(30) NOT NULL" +
                    ")");
            String choice = Order.forceToMakeChoice();
            while (!Objects.equals(choice, "exit")) {
                mealService.actionMap.get(choice).run();
                choice = Order.forceToMakeChoice();
            }
            mealService.actionMap.get(choice).run();

            // Possibility of showing all meals
//          final List<Meal> meals = mealService.mealRepository.findMeals();
//            meals.forEach(
//                    meal -> meal.getIngredientsList()
//                            .addAll(repository.findMealIngredients(meal.getId()))
//            );
//          meals.forEach(System.out::println);
//            final List<DayPlan> weekPlan = mealService.getWeekPlan();
//            System.out.println(weekPlan);
//            System.out.println("Printowanie planu: ");
//            weekPlan.forEach(DayPlan::printDayPlan);
        } catch (SQLException e) {
            throw new InvalidDataBaseProcessException("Exception during database process, " + e.getMessage());
        }
    }
}