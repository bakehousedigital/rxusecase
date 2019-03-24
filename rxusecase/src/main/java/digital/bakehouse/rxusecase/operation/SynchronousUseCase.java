package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

/**
 * Abstraction for use-cases that represent a synchronous operation
 * which starts when its {@link Observable} is being subscribed to
 * and completes with either success or failure depending
 * on whether the wrapped {@link Synchronous#act(Object)} method
 * is returning something or throwing an exception.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public abstract class SynchronousUseCase<I, O> extends RxUseCase<I, O>
        implements Synchronous<I, O> {
    @Override
    protected final Observable<Response<O>> execute(I input) {
        return toRx(this, input);
    }
}
