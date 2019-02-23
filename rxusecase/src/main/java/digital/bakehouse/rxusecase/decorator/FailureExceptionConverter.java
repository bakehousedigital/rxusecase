package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Failure;
import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.FailureException;
import io.reactivex.Observable;

import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;

@SuppressWarnings("unused")
public final class FailureExceptionConverter implements UseCaseDecorator {

    private Failure fallbackFailure;

    private FailureExceptionConverter() {
    }

    private FailureExceptionConverter(Failure fallbackFailure) {
        this.fallbackFailure = fallbackFailure;
    }

    public static FailureExceptionConverter getDefault() {
        return new FailureExceptionConverter();
    }

    public static FailureExceptionConverter getWithFallback(Failure fallbackFailure) {
        return new FailureExceptionConverter(fallbackFailure);
    }

    @Override
    public final <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                   Request<I> ignored) {
        return origin.onErrorResumeNext(throwable -> {
            if (throwable instanceof FailureException) {
                return failureResponse(((FailureException) throwable).getFailure());
            } else if (fallbackFailure != null) {
                return failureResponse(fallbackFailure);
            }

            return error(throwable);
        });
    }

    private static <O> Observable<Response<O>> failureResponse(Failure failure) {
        Response<O> failureResponse = Response.fail(failure);
        return just(failureResponse);
    }
}
