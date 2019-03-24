package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

/**
 * Abstraction for use-cases that represent an asynchronous operation
 * which starts when its {@link Observable} is being subscribed to
 * and completes with either success or failure by invoking the
 * {@link Asynchronous.Callback} respective methods.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public abstract class AsynchronousUseCase<I, O> extends RxUseCase<I, O>
        implements Asynchronous<I, O> {
    @Override
    protected final Observable<Response<O>> execute(I input) {
        return toRx(this, input);
    }
}
