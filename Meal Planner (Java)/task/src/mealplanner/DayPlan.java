package mealplanner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DayPlan {
    private final WeekDay weekDay;
    private final Map<MealCategory, String> mealsMap; // key=category; value=meal name
    private final List<Integer> mealIdsList;

    public DayPlan(WeekDay weekDay) {
        this.weekDay = weekDay;
        this.mealsMap = new LinkedHashMap<>();
        this.mealIdsList = new ArrayList<>();
    }

    public Map<MealCategory, String> getMealsMap() {
        return mealsMap;
    }

    public List<Integer> getMealIdsList() {
        return mealIdsList;
    }

    public void printDayPlan() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.weekDay.getDayName())
                .append("\n");
        if (!this.mealsMap.isEmpty()) {
            for (Map.Entry<MealCategory, String> entry : this.mealsMap.entrySet()) {
                stringBuilder.append(entry.getKey().getCategoryName())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }
        stringBuilder.append("\n");
        System.out.println(stringBuilder);
    }
}