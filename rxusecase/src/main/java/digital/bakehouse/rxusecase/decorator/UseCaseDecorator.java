package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public interface UseCaseDecorator {
    @NonNull
    <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                            Request<I> request);
}
