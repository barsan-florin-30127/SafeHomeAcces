package isp.lab7.safehome;

public class TooManyAttemptsException extends Exception {
    public TooManyAttemptsException() {
        System.out.println("To many attempts to introduce right pin.");
    }
}
