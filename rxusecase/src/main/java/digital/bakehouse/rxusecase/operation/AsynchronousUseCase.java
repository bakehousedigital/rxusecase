package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

public abstract class AsynchronousUseCase<I, O> extends RxUseCase<I, O>
        implements Asynchronous<I, O> {
    @Override
    protected final Observable<Response<O>> execute(I input) {
        return toRx(this, input);
    }
}
