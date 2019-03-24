package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Failure;
import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.FailureException;
import io.reactivex.Observable;

import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;

/**
 * Decorator that maps exceptions thrown by decorated use-cases
 * to {@link Failure} objects.
 * {@link FailureException} will always get mapped to a {@link Failure}.
 * If an {@link #exceptionMapper} is defined, it will be used to map
 * all the other exceptions. Otherwise (or if it returns null failure for an
 * exception) the exception will be propagated down the stream.
 */
public final class FailureConverter implements UseCaseDecorator {

    private Mapper exceptionMapper;

    private FailureConverter() {
    }

    private FailureConverter(Mapper exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
    }

    /**
     * Factory method to create a converter which converts
     * only instances of the {@link FailureException} into {@link Failure}
     * objects and propagates the other exception types does the stream.
     *
     * @return Default failure converter
     */
    public static FailureConverter getDefault() {
        return new FailureConverter();
    }

    /**
     * Factory method to create a converter which converts the exceptions
     * emitted by the decorated use-cases into {@link Failure} objects
     * using the passed mapper.
     *
     * @param exceptionToFailureMapper Exception mapper
     * @return Mapping failure converter
     */
    public static FailureConverter get(Mapper exceptionToFailureMapper) {
        return new FailureConverter(exceptionToFailureMapper);
    }

    /**
     * Factory method to create a converter which converts all
     * exceptions emitted by the decorated use-cases to {@link Failure} objects:
     * - {@link FailureException} to their respective {@link Failure} objects
     * - any other exception to the passed fallback {@link Failure}
     *
     * @param fallbackFailure Fallback failure
     * @return Fallback converter
     */
    public static FailureConverter getWithFallback(Failure fallbackFailure) {
        return new FailureConverter(exception -> fallbackFailure);
    }

    @Override
    public final <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                         Request<I> ignored) {
        return origin.onErrorResumeNext(throwable -> {
            if (throwable instanceof FailureException) {
                return failureResponse(((FailureException) throwable).getFailure());
            }
            if (exceptionMapper != null) {
                Failure mappedFailure = exceptionMapper.map(throwable);
                if (mappedFailure != null) {
                    return failureResponse(mappedFailure);
                }
            }

            return error(throwable);
        });
    }

    private static <O> Observable<Response<O>> failureResponse(Failure failure) {
        Response<O> failureResponse = Response.fail(failure);
        return just(failureResponse);
    }

    /**
     * Exception to {@link Failure} mapper.
     */
    interface Mapper {
        /**
         * Map exception to{@link Failure}.
         *
         * @param exception Exception to map
         * @return Mapped failure
         */
        Failure map(Throwable exception);
    }
}
