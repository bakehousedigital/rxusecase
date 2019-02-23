package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import io.reactivex.Observable;

public abstract class ContinuousUseCase<I, O> extends RxUseCase<I, O>
        implements Continuous<I, O> {

    @Override
    protected Observable<Response<O>> execute(I input) {
        return toRx(this, input);
    }
}
