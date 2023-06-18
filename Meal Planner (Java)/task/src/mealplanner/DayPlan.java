package mealplanner;

import java.util.LinkedHashMap;
import java.util.Map;

public class DayPlan {
    private final WeekDay weekDay;
    private final Map<MealCategory, String> mealsMap; //key=category; value=meal name

    public DayPlan(WeekDay weekDay) {
        this.weekDay = weekDay;
        this.mealsMap = new LinkedHashMap<>();
    }

    public WeekDay getDayName() {
        return weekDay;
    }

    public Map<MealCategory, String> getMealsMap() {
        return mealsMap;
    }

    public void printDayPlan() {
        StringBuilder stringBuilder = new StringBuilder();
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
