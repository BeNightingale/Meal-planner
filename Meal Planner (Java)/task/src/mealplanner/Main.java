package mealplanner;

import mealplanner.utils.SqlUtils;

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
             final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            final MealService mealService = new MealService(connection);

//           ------ Possibility of cleaning everything in tables ------
//            statement.executeUpdate("DROP SEQUENCE IF EXISTS meals_sequence");
//            statement.executeUpdate("DROP TABLE meals");
//            statement.executeUpdate("DROP TABLE ingredients");
//            statement.executeUpdate("DROP TABLE plan");
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS meals_sequence start 1 increment 1 cache 1");
            SqlUtils.createTableMeals(statement);

//            statement.executeUpdate("DROP SEQUENCE IF EXISTS ingredients_sequence");
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS ingredients_sequence start 1 increment 1 cache 1");
            SqlUtils.createTableIngredients(statement);
            SqlUtils.createTablePlan(statement);

            String choice = Order.forceToMakeChoice();
            while (!Objects.equals(choice, "exit")) {
                mealService.actionMap.get(choice).run();
                choice = Order.forceToMakeChoice();
            }
            mealService.actionMap.get(choice).run();

            // ----- Possibility of showing all meals -----
//            Utils.showAllMeals(mealService, connection);
//            Utils.showMealsWeekPlan(mealService);
        } catch (SQLException e) {
            throw new InvalidDataBaseProcessException("Exception during database process, " + e.getMessage());
        }
    }
}