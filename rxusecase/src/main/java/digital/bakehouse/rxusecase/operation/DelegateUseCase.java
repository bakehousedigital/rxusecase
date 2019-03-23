package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

public class DelegateUseCase<I, O> extends RxUseCase<I, O> {

    private RxSource<I, O> delegate;

    public DelegateUseCase(RxSource<I, O> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Observable<Response<O>> execute(I input) {
        return delegate.getObservable(input);
    }
}
