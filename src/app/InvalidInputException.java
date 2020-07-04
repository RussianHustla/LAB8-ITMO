package app;

/**
 * Ошибка некорректного ввода
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
