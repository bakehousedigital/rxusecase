package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * Decorator of use-case observables.
 * Can be added globally for all use-cases or locally for a specific use-case.
 * Can be used to add filtering, retry mechanism, caching, threading, logging etc. to the use-cases.
 */
public interface UseCaseDecorator {

    /**
     * Decorate the passed observable stream with additional functionality.
     *
     * @param origin  Observable stream to decorate
     * @param request Request object
     * @param <I>     Input type
     * @param <O>     Output type
     * @return Decorated observable stream
     */
    @NonNull
    <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                            Request<I> request);
}
