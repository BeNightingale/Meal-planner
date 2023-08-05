package mealplanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class MealService {


    private final Runnable add = this::addMeal;
    private final Runnable show = this::showMeals;
    private final Runnable exit = this::exit;
    private final Runnable plan = this::planMeals;
    private final Runnable save = this::saveShoppingList;

    protected final Map<String, Runnable> actionMap = Map.ofEntries(
            entry("add", add),
            entry("show", show),
            entry("exit", exit),
            entry("plan", plan),
            entry("save", save));
    final Connection connection;
    private final MealRepository mealRepository;
    private final Repository repository;


    public MealService(Connection connection) {
        this.connection = connection;
        this.mealRepository = new MealRepository(connection);
        this.repository = new Repository(connection);
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
        final List<Meal> mealList = mealRepository.findMealsByCategory(category);
        mealList.forEach(
                meal -> meal.getIngredientsList()
                        .addAll(repository.findMealIngredients(meal.getId()))
        );
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
        try {
            repository.cleanPlanTable();
        } catch (SQLException sqlEx) {
            System.out.println("Nie można wyczyścić tabeli plan" + sqlEx);
        }
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
        final Map<String, Integer> mealNamesMap = mealRepository.findMealNamesAndIdsByCategory(mealCategoryName);
        final List<String> mealChoice = Order.makeCorrectMealNameChoice(mealNamesMap, weekDayName, mealCategoryName);
        if (mealChoice.isEmpty()) {
            System.out.println("mealChoice is empty!!!");
        } else {
            repository.savePlannedMeal(mealChoice.get(0), mealCategoryName, Integer.parseInt(mealChoice.get(1)), weekDayName);
        }
    }

    protected List<DayPlan> getWeekPlan() {
        return Arrays.stream(WeekDay.values()).map(repository::getPlanForDay).toList();
    }

    protected List<Integer> getMealsIdsListForWeekPlan(List<DayPlan> weekPlan) {
        return weekPlan.stream().flatMap(dayPlan -> dayPlan.getMealIdsList().stream()).toList();
    }

    private boolean areMealsPlannedForWeek() {
        return repository.getPlannedMealsNumberInWeek() == 21;
    }

    private void saveShoppingList() {
        final List<DayPlan> weekPlan = getWeekPlan();
        if (!areMealsPlannedForWeek()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }
        final String fileName = Order.getShoppingListFileName();
        final List<Integer> mealsIdsList = getMealsIdsListForWeekPlan(weekPlan);
        final Map<Integer, Integer> mealsIdsGroupedByEqualMealsIdMap = Utils.groupListByEqualValues(mealsIdsList);

        final List<Integer> distinctMealsIdsList = Utils.getListWithDistinctValues(mealsIdsList);
        final Map<String, Integer> ingredientsGroupedByEqualValueMap = new HashMap<>();

        distinctMealsIdsList.forEach(mealId -> {
                    final List<String> mealIngredients = repository.findMealIngredients(mealId);
                    final Integer mealsNumberWithThisId = mealsIdsGroupedByEqualMealsIdMap.get(mealId) == null ?
                            0 : mealsIdsGroupedByEqualMealsIdMap.get(mealId);
                    Utils.completeMapWithGroupingValues(ingredientsGroupedByEqualValueMap, mealIngredients, mealsNumberWithThisId);
                }
        );
        final String content = Order.printShoppingList(ingredientsGroupedByEqualValueMap);
        try {
            createFile(fileName, content);
        } catch (Exception e) {
            System.out.println("Error");
        }
        System.out.println("Saved!");
    }

    private void exit() {
        System.out.println("Bye!");
    }

    private void insertMeal(Meal meal) {
        int mealId = mealRepository.insertMealInformation(meal);
        repository.insertIngredientInformation(meal, mealId);
    }

    private void createFile(String fileName, String content) throws IOException {
        final File file = new File(fileName); // for JetBrain Academy tests
        //final File file = new File("./Meal Planner (Java)/task/src/resources", fileName); // saving for me
        if (file.createNewFile()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.append(content);
            }
        }
    }
}