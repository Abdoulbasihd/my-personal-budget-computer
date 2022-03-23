package cm.abimmobiledev.mybudgetizer.exception;

public class BudgetizerGeneralException extends Exception {
    private String message;
    private int code;

    public BudgetizerGeneralException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BudgetizerGeneralException() {
    }

    public BudgetizerGeneralException(Throwable cause) {
        super(cause);
    }

    public BudgetizerGeneralException(String message) {
        super(message);
        this.message = message;
    }

}
