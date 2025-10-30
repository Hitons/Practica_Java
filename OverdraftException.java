package Bank;

public class OverdraftException extends Exception {

    private double deficit;
    private String message;

    public OverdraftException(String message, double deficit) {
        this.deficit = deficit;
        this.message = message;
    }

    public double getDeficit() {
        return deficit;
    }

    public String getMessage() {
        return message;
    }
}