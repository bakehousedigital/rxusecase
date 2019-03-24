package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import digital.bakehouse.rxusecase.toolbox.Objects;
import io.reactivex.Observable;

/**
 * Abstraction for use-cases that represent an observable stream.
 * It is useful for situations when extending {@link RxUseCase} classes
 * is not an option, and the other operations are not suitable.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public class DelegateUseCase<I, O> extends RxUseCase<I, O> {

    private RxSource<I, O> delegate;

    /**
     * Create a delegating use-case.
     *
     * @param delegate Holder of source observable
     */
    public DelegateUseCase(RxSource<I, O> delegate) {
        Objects.requireNonNull(delegate, "Delegate cannot be null!");
        this.delegate = delegate;
    }

    @Override
    protected Observable<Response<O>> execute(I input) {
        return delegate.getObservable(input);
    }
}
