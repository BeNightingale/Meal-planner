package mealplanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private Utils() {
        // sth
    }

    static <T> Map<T, Integer> groupListByEqualValues(List<T> list) {
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

    static <T> void completeMapWithGroupingValues(Map<T, Integer> groupedValuesMap, List<T> list, Integer repetitionsNumber) {
        list.forEach(i -> groupedValuesMap.merge(i, repetitionsNumber, Integer::sum));
    }

    static <T> List<T> getListWithDistinctValues(List<T> list) {
        return list.stream().distinct().toList();
    }
}