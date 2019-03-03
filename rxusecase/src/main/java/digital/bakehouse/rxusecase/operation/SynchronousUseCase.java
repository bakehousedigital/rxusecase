package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

public abstract class SynchronousUseCase<I, O> extends RxUseCase<I, O>
        implements Synchronous<I, O> {
    @Override
    protected final Observable<Response<O>> execute(I input) {
        return toRx(this, input);
    }
}
