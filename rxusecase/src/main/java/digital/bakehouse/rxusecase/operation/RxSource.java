package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;

public interface RxSource<I, O> {
    Observable<Response<O>> getObservable(I input);
}
