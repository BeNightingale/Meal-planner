package mealplanner;

import java.sql.*;
import java.util.*;

import static java.util.Map.entry;

public class MealService {

    private final Runnable add = this::addMeal;
    private final Runnable show = this::showMeals;
    private final Runnable exit = this::exit;

    protected final Map<String, Runnable> actionMap = Map.ofEntries(
            entry("add", add),
            entry("show", show),
            entry("exit", exit));
    final Connection connection;

    public MealService(Connection connection) {
        this.connection = connection;
    }

    private void addMeal() {
        final Meal meal = new Meal(
                Order.specifyMeal(),
                Order.getMealName(),
                Order.getIngredients()
        );
        try {
            insertMeal(meal);
        } catch (Exception ex) {
            System.out.println("It wasn't possible to insert to the database the meal = " + meal + " " + ex);
        }
    }

    private void showMeals() {
        final String category = Order.makeCorrectPrintChoice();
        final List<Meal> mealList = findMealsByCategory(category);
        if (mealList.isEmpty()) {
            System.out.println("No meals found.");
            return;
        }
        System.out.println("Category: " + category);
        if (mealList.size() == 1) {
            mealList.get(0).printMeal();
            return;
        }
        System.out.println();
        for (Meal meal : mealList) {
            meal.printMeal();
            System.out.println();
        }
    }

    private List<Meal> findMealsByCategory(String category) {
        final String selectMealsByCategorySql = "SELECT * FROM meals WHERE category = ?";
        final List<Meal> mealList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectMealsByCategorySql)) {
            preparedStatement.setString(1, category);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Meal meal = asMeal(resultSet);
                mealList.add(meal);
            }
        } catch (SQLException sqlEx) {
            System.out.println("It was't possible to get meals list from database " + sqlEx);
            return Collections.emptyList();
        }
        mealList.forEach(
                meal -> meal.getIngredientsList()
                        .addAll(findMealIngredients(meal.getId()))
        );
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
            System.out.println("It was't possible to get meals list from database " + sqlEx);
            return Collections.emptyList();
        }
        mealList.forEach(
                meal -> meal.getIngredientsList()
                        .addAll(findMealIngredients(meal.getId()))
        );
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

    private List<String> findMealIngredients(int mealId) {
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

    private void exit() {
        System.out.println("Bye!");
    }

    private void insertMeal(Meal meal) {
        int mealId = insertMealInformation(meal);
        insertIngredientInformation(meal, mealId);
    }

    // returns mealId or -1 when error
    private int insertMealInformation(Meal meal) {
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

    private void insertIngredientInformation(Meal meal, int mealId) {
        if (meal == null)
            return;
        final String insertSqlIntoIngredientsTable = "INSERT INTO ingredients (ingredient,ingredient_id, meal_id)" +
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
}
