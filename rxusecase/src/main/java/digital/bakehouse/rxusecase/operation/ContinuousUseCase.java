package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

/**
 * Abstraction for use-cases that represent a continuous operation
 * which starts when its {@link Observable} is being subscribed to,
 * can emit items continuously, and completes normally or with failure by invoking the
 * {@link Continuous.Notifier} respective methods.
 * When its {@link Observable} is being un-subscribed from, the
 * {@link Continuous.Notifier#cancel(Object)} method will get invoked.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public abstract class ContinuousUseCase<I, O> extends RxUseCase<I, O>
        implements Continuous<I, O> {

    @Override
    protected final Observable<Response<O>> execute(I input) {
        return toRx(this, input);
    }
}
