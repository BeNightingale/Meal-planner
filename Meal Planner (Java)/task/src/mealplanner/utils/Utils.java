package mealplanner.utils;

import mealplanner.model.DayPlan;
import mealplanner.model.Meal;
import mealplanner.MealService;
import mealplanner.Repository;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private Utils() {
        // sth
    }

    public static <T> Map<T, Integer> groupListByEqualValues(List<T> list) {
        final Map<T, Integer> groupedValuesMap = new HashMap<>();
        list.forEach(i -> {
            Integer occurrencesNumber = groupedValuesMap.get(i);
            if (occurrencesNumber == null) {
                groupedValuesMap.put(i, 1);
            } else {
                groupedValuesMap.put(i, ++occurrencesNumber);
            }
        });
        return groupedValuesMap;
    }

    public static <T> void completeMapWithGroupingValues(Map<T, Integer> groupedValuesMap, List<T> list, Integer repetitionsNumber) {
        list.forEach(i -> groupedValuesMap.merge(i, repetitionsNumber, Integer::sum));
    }

    public static <T> List<T> getListWithDistinctValues(List<T> list) {
        return list.stream().distinct().toList();
    }

    static void showAllMeals(MealService mealService, Connection connection) {
        final Repository repository = new Repository(connection);
        final List<Meal> meals = mealService.getMealRepository().findMeals();
        meals.forEach(
                meal -> meal.getIngredientsList()
                        .addAll(repository.findMealIngredients(meal.getId()))
        );
        meals.forEach(System.out::println);
    }

    static void showMealsWeekPlan(MealService mealService) {
        final List<DayPlan> weekPlan = mealService.getWeekPlan();
        System.out.println(weekPlan);
        System.out.println("Meal Plan for the whole week: ");
        weekPlan.forEach(DayPlan::printDayPlan);
    }
}