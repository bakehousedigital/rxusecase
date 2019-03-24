package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;

/**
 * Simple log decorator.
 * Useful to enable global logging of all the use-cases.
 */
public final class LogDecorator implements UseCaseDecorator {

    private static final String DEFAULT_DELIMITER = "~~";
    private static final LogOutput SYSTEM_OUTPUT = System.out::println;

    private final LogOutput logOutput;
    private final String delimiter;

    private LogDecorator(LogOutput logOutput, String delimiter) {
        this.logOutput = logOutput;
        this.delimiter = delimiter;
    }

    /**
     * Get default logger.
     *
     * @return Default logger
     */
    public static LogDecorator getDefault() {
        return getDefault(DEFAULT_DELIMITER);
    }

    /**
     * Get default logger using the passed string as
     * log message delimiter.
     *
     * @param delimiter Log message delimiter
     * @return Delimiter logger
     */
    public static LogDecorator getDefault(String delimiter) {
        return new LogDecorator(SYSTEM_OUTPUT, delimiter);
    }

    /**
     * Get logger instance which uses the passed output
     * to log the messages.
     *
     * @param logOutput Output for logging messages
     * @return Output logger
     */
    public static LogDecorator getWithOutput(LogOutput logOutput) {
        return getWithOutput(logOutput, DEFAULT_DELIMITER);
    }

    /**
     * Get logger instance which uses the passed output to
     * log the messages. The passed string will be used as
     * log message delimiter.
     *
     * @param logOutput Output for logging messages
     * @param delimiter Log message delimiter
     * @return Delimiter logger
     */
    public static LogDecorator getWithOutput(LogOutput logOutput, String delimiter) {
        return new LogDecorator(logOutput, delimiter);
    }

    @Override
    public final <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                         Request<I> request) {
        TimeHolder timeHolder = new TimeHolder();
        return origin
                .doOnSubscribe(disposable -> {
                    timeHolder.initialize();
                    logAction(request, "Subscribe");
                })
                .doOnNext(response -> {
                    if (response.isSuccessful()) {
                        logAction(request, "Success = %s", response.getData());
                    } else {
                        logAction(request, "Failure = %s", response.getFailure());
                    }
                })
                .doOnError(throwable ->
                        logAction(request, "Exception = %s", throwable))
                .doOnComplete(() ->
                        logAction(request, "Complete"))
                .doOnTerminate(() ->
                        logAction(request, "Finish %s Elapsed Time: %sms",
                                delimiter, timeHolder.diff()))
                ;
    }

    private void logAction(Request request, String message, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            message = String.format(message, arguments);
        }
        String logMessage = String.format("%2$s(%3$s) %1$s %4$s",
                delimiter,
                request.getOrigin(),
                request.getInput(),
                message);

        logOutput.log(logMessage);
    }

    private static class TimeHolder {
        private long timeMillis;

        private long diff() {
            return System.currentTimeMillis() - timeMillis;
        }

        private void initialize() {
            timeMillis = System.currentTimeMillis();
        }
    }

    /**
     * Log output useful for customizing the way
     * messages are logged.
     */
    public interface LogOutput {
        /**
         * Log message.
         *
         * @param message Message to log
         */
        void log(String message);
    }
}
