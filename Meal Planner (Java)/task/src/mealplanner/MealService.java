package mealplanner;

import java.sql.*;
import java.util.*;

import static java.util.Map.entry;

public class MealService {

    private static final String ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS = "It was't possible to get information about meals list from database ";
    private final Runnable add = this::addMeal;
    private final Runnable show = this::showMeals;
    private final Runnable exit = this::exit;
    private final Runnable plan = this::planMeals;

    protected final Map<String, Runnable> actionMap = Map.ofEntries(
            entry("add", add),
            entry("show", show),
            entry("exit", exit),
            entry("plan", plan));
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

    private void planMeals() {
        for (WeekDay day : WeekDay.values()) {
            final String dayName = day.getDayName();
            System.out.println(dayName);
            planDay(day);
            System.out.printf("Yeah! We planned the meals for %s.%n%n", dayName);
        }
        final List<DayPlan> weekPlan = getWeekPlan();
        weekPlan.forEach(DayPlan::printDayPlan);
    }

    private void planDay(WeekDay weekDay) {
        for (MealCategory category : MealCategory.values()) {
            planCategoryForDay(weekDay, category);
        }
    }

    private void planCategoryForDay(WeekDay weekDay, MealCategory mealCategory) {
        final String mealCategoryName = mealCategory.getCategoryName();
        final String weekDayName = weekDay.getDayName();
        final Map<String, Integer> mealNamesMap = findMealNamesByCategory(mealCategoryName);
        final List<String> mealChoice = Order.makeCorrectMealNameChoice(mealNamesMap, weekDayName, mealCategoryName);
        if (mealChoice.isEmpty()) {
            System.out.println("mealChoice is empty!!!");
        } else {
            savePlannedMeal(mealChoice.get(0), mealCategoryName, Integer.parseInt(mealChoice.get(1)), weekDayName);
        }
    }

    private List<DayPlan> getWeekPlan() {
        return Arrays.stream(WeekDay.values()).map(this::getPlanForDay).toList();
    }

    private Map<String, Integer> findMealNamesByCategory(String category) {
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

    private void savePlannedMeal(String mealName, String mealCategory, Integer mealId, String weekDayName) {
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

    private List<Meal> findMealsByCategory(String category) {
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
            System.out.println(ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS + sqlEx);
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

    private DayPlan getPlanForDay(WeekDay weekDay) {
        final String selectMealsSql = "SELECT meal_option, meal_category FROM plan WHERE day = ?";
        final DayPlan dayPlan = new DayPlan(weekDay);
        try (final PreparedStatement preparedStatement = connection.prepareStatement(selectMealsSql)) {
            preparedStatement.setString(1, weekDay.getDayName());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String mealName = resultSet.getString(1);
                final String mealCategory = resultSet.getString(2);
                dayPlan.getMealsMap().put(MealCategory.valueOf(mealCategory.toUpperCase()), mealName);
            }
        } catch (SQLException sqlEx) {
            System.out.println(ERROR_DURING_GETTING_INFORMATION_ABOUT_MEALS + sqlEx);
            return dayPlan;
        }
        return dayPlan;
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
}
