package mealplanner;

import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtils {

    static void createTableIngredients(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                "ingredient VARCHAR(100) NOT NULL, " +
                "ingredient_id INTEGER NOT NULL PRIMARY KEY, " +
                "meal_id INTEGER NOT NULL" +
                ")");
    }

    static void createTablePlan(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                "meal_option VARCHAR(100), " +
                "meal_category VARCHAR(30) NOT NULL, " +
                "meal_id INTEGER NOT NULL, " +
                "day VARCHAR(30) NOT NULL" +
                ")");
    }

    static void createTableMeals(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                "category VARCHAR(30) NOT NULL, " +
                "meal VARCHAR(100) NOT NULL, " +
                "meal_id INTEGER NOT NULL PRIMARY KEY" +
                ")");
    }
}