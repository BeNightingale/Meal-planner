package mealplanner;

public class InvalidDataBaseProcessException extends RuntimeException {
    public InvalidDataBaseProcessException(String message) {
        super(message);
    }
}