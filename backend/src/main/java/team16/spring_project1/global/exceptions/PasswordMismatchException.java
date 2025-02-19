package team16.spring_project1.global.exceptions;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String msg) {
        super(msg);
    }
}
