package mealplanner;

public enum WeekDay {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String dayName;

    WeekDay(String dayName) {
        this.dayName = dayName;
    }

    public String getDayName() {
        return dayName;
    }
}