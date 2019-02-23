package digital.bakehouse.rxusecase;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FailureException extends Exception {
    private Failure failure;

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
