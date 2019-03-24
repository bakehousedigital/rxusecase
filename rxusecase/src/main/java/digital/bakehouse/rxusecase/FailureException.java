package digital.bakehouse.rxusecase;

/**
 * Exception class acting as a wrapper for {@link Failure} objects.
 */
public class FailureException extends Exception {
    private Failure failure;

    /**
     * Build exception with the passed message.
     * Internally the created {@link Failure} object will
     * set both its code and its message to the passed value.
     *
     * @param message Exception message
     */
    public FailureException(String message) {
        this(new Failure(message, message));
    }

    /**
     * Build exception with failure.
     *
     * @param failure Failure to wrap
     */
    public FailureException(Failure failure) {
        super(failure.getMessage());
        this.failure = failure;
    }

    /**
     * Build exception with failure and a cause.
     *
     * @param failure Failure to wrap
     * @param cause   Cause of the exception
     */
    public FailureException(Failure failure, Throwable cause) {
        super(cause);
        this.failure = failure;
    }

    public Failure getFailure() {
        return failure;
    }

    /**
     * Factory method to wrap the passed error code and message into
     * a {@link Failure} object enclosed by the exception.
     * This method is useful to remove the need of instantiating
     * {@link Failure} objects outside this class.
     *
     * @param errorCode    Failure code
     * @param errorMessage Failure message
     * @return Exception
     */
    public static FailureException create(String errorCode, String errorMessage) {
        return new FailureException(new Failure(errorCode, errorMessage));
    }
}
