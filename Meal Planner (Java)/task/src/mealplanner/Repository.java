package mealplanner;

import mealplanner.model.DayPlan;
import mealplanner.model.Meal;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Repository {

    final Connection connection;

    public Repository(Connection connection) {
        this.connection = connection;
    }

    protected void savePlannedMeal(String mealName, String mealCategory, Integer mealId, String weekDayName) {
        if (mealCategory == null || mealCategory.isEmpty() || mealId == null || weekDayName == null || weekDayName.isEmpty()) {
            throw new IllegalArgumentException(String.format("Planning this meal is impossible - empty parameter; " +
                    "mealCategory = %s, mealId = %d, weekDayName = %s.%n", mealCategory, mealId, weekDayName));
        }
        final String insertMealPlanSql = "INSERT INTO plan (meal_option, meal_category, meal_id, day) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertMealPlanSql)) {
            preparedStatement.setString(1, mealName);
            preparedStatement.setString(2, mealCategory);
            preparedStatement.setInt(3, mealId);
            preparedStatement.setString(4, weekDayName);
            preparedStatement.executeUpdate();
        } catch (SQLException | NullPointerException ex) {
            System.out.println("Error during inserting planned meal " + ex);
        }
    }

    protected DayPlan getPlanForDay(WeekDay weekDay) {
        final String selectMealsSql = "SELECT meal_option, meal_category, meal_id FROM plan WHERE day = ?";
        final DayPlan dayPlan = new DayPlan(weekDay);
        try (final PreparedStatement preparedStatement = connection.prepareStatement(selectMealsSql)) {
            preparedStatement.setString(1, weekDay.getDayName());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String mealName = resultSet.getString(1);
                final String mealCategory = resultSet.getString(2);
                final Integer mealId = resultSet.getInt(3);
                dayPlan.getMealsMap().put(MealCategory.valueOf(mealCategory.toUpperCase()), mealName);
                dayPlan.getMealIdsList().add(mealId);
            }
        } catch (SQLException sqlEx) {
            System.out.println("Error during getting information about meals in table plan " + sqlEx);
            return dayPlan;
        }
        return dayPlan;
    }

    protected int getPlannedMealsNumberInWeek() {
        final String selectSql = "SELECT COUNT(*) FROM plan";
        int count = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            throw new InvalidDataBaseProcessException(e.getMessage());
        }
    }

    protected List<String> findIngredientsForMealList(List<Integer> mealsIdsList) {
        final String joinedList = mealsIdsList
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        final String selectIngredientsSql = "SELECT (ingredient) FROM ingredients WHERE meal_id IN (" + joinedList + ")";
        final List<String> mealIngerdientsList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectIngredientsSql)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String ingredient = resultSet.getString(1);
                mealIngerdientsList.add(ingredient);
            }
            return mealIngerdientsList;
        } catch (SQLException sqlEx) {
            System.out.println("It wasn't possible to get ingredients for meals with ids in the list " + mealsIdsList + ", " + sqlEx);
            return Collections.emptyList();
        }
    }

    protected void insertIngredientInformation(Meal meal, int mealId) {
        if (meal == null)
            return;
        final String insertSqlIntoIngredientsTable = "INSERT INTO ingredients (ingredient, ingredient_id, meal_id)" +
                " VALUES (?, nextval('ingredients_sequence'), ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSqlIntoIngredientsTable)) {
            final List<String> ingredientsList = meal.getIngredientsList();
            for (String ingredient : ingredientsList) {
                preparedStatement.setString(1, ingredient);
                preparedStatement.setInt(2, mealId);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("error!   " + e);
        }
    }

    public List<String> findMealIngredients(int mealId) {
        final String selectIngredientsSql = "SELECT (ingredient) FROM ingredients WHERE meal_id = (?)";
        final List<String> mealIngerdientsList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectIngredientsSql)) {
            preparedStatement.setInt(1, mealId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String ingredient = resultSet.getString(1);
                mealIngerdientsList.add(ingredient);
            }
            return mealIngerdientsList;
        } catch (SQLException sqlEx) {
            System.out.println("It wasn't possible to get ingredients for a meal with id " + mealId + ", " + sqlEx);
            return Collections.emptyList();
        }
    }

    protected void cleanPlanTable() throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE plan");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                    "meal_option VARCHAR(100), " +
                    "meal_category VARCHAR(30) NOT NULL, " +
                    "meal_id INTEGER NOT NULL, " +
                    "day VARCHAR(30) NOT NULL" +
                    ")");
        }
    }
}