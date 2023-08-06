package mealplanner;

import mealplanner.model.Meal;

import java.sql.*;
import java.util.*;

public class MealRepository {

    final Connection connection;

    public MealRepository(Connection connection) {
        this.connection = connection;
    }

    private static final String ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS = "It was't possible to get information about meals list from database ";


    // returns mealId or -1 when error
    protected int insertMealInformation(Meal meal) {
        if (meal == null) {
            return -1;
        }
        final String insertSqlIntoMealsTable = "INSERT INTO meals (category, meal, meal_id) VALUES " +
                "(?, ?, nextval('meals_sequence')) RETURNING meal_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSqlIntoMealsTable)) {
            preparedStatement.setString(1, meal.getCategory());
            preparedStatement.setString(2, meal.getName());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt(1); //"RETURNING" returns a table with 1 column which has meal_id
        } catch (SQLException | NullPointerException ex) {
            System.out.println("Error " + ex);
            return -1;
        }
        return -1;
    }

    protected Map<String, Integer> findMealNamesAndIdsByCategory(String category) {
        final String selectMealsByCategorySql = "SELECT meal, meal_id FROM meals WHERE category = ?";
        final Map<String, Integer> mealNameMap = new LinkedHashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectMealsByCategorySql)) {
            preparedStatement.setString(1, category.toLowerCase());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String mealName = resultSet.getString(1);
                final Integer mealId = resultSet.getInt(2);
                mealNameMap.put(mealName, mealId); // mealName should be unique for a given category
            }
        } catch (SQLException sqlEx) {
            System.out.println(ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS + sqlEx);
            return Collections.emptyMap();
        }
        return mealNameMap;
    }

    protected List<Meal> findMealsByCategory(String category) {
        final String selectMealsByCategorySql = "SELECT * FROM meals WHERE category = (?)";
        final List<Meal> mealList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectMealsByCategorySql)) {
            preparedStatement.setString(1, category);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Meal meal = asMeal(resultSet);
                mealList.add(meal);
            }
        } catch (SQLException sqlEx) {
            System.out.println(ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS + sqlEx);
            return Collections.emptyList();
        }
        return mealList;
    }

    public List<Meal> findMeals() {
        final String selectMealsSql = "SELECT * FROM meals";
        final List<Meal> mealList = new ArrayList<>();
        try (final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(selectMealsSql);
            while (resultSet.next()) {
                final Meal meal = asMeal(resultSet);
                mealList.add(meal);
            }
        } catch (SQLException sqlEx) {
            System.out.println(ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS + sqlEx);
            return Collections.emptyList();
        }
        return mealList;
    }

    private Meal asMeal(ResultSet resultSet) throws SQLException {
        return new Meal(
                resultSet.getInt(3),
                resultSet.getString(1),
                resultSet.getString(2),
                new ArrayList<>()
        );
    }
}