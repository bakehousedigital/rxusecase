package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;

@SuppressWarnings("WeakerAccess")
public final class LogDecorator implements UseCaseDecorator {

    private static final String DELIMITER = "~~";
    private static final LogOutput SYSTEM_OUTPUT = System.out::println;

    private LogOutput logOutput;

    private LogDecorator(LogOutput logOutput) {
        this.logOutput = logOutput;
    }

    public static LogDecorator getDefault() {
        return new LogDecorator(SYSTEM_OUTPUT);
    }

    public static LogDecorator getWithOutput(LogOutput logOutput) {
        return new LogDecorator(logOutput);
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
                                DELIMITER, timeHolder.diff()))
                ;
    }

    private void logAction(Request request, String message, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            message = String.format(message, arguments);
        }
        String logMessage = String.format("%2$s(%3$s) %1$s %4$s",
                DELIMITER,
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

    public interface LogOutput {
        void log(String message);
    }
}
