package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;

/**
 * Interface acting as a holder of a source observable.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public interface RxSource<I, O> {

    /**
     * Return the source {@link Observable}.
     *
     * @param input Operation input
     * @return Observable stream
     */
    Observable<Response<O>> getObservable(I input);
}
