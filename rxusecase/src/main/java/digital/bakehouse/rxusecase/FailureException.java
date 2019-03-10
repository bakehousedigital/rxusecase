package digital.bakehouse.rxusecase;

public class FailureException extends Exception {
    private Failure failure;

    public FailureException(String message) {
        this(new Failure(message, message));
    }

    public FailureException(Failure failure) {
        super(failure.getMessage());
        this.failure = failure;
    }

    public FailureException(Failure failure, Throwable cause) {
        super(cause);
        this.failure = failure;
    }

    public Failure getFailure() {
        return failure;
    }

    public static FailureException create(String errorCode, String errorMessage) {
        return new FailureException(new Failure(errorCode, errorMessage));
    }
}
