package digital.bakehouse.test;

import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class Observables {

    public static <O> void assertValue(Observable<Response<O>> operationStream, Response<O> expectedResponse) {
        TestObserver<Response<O>> observer = operationStream.test();
        observer.awaitTerminalEvent();
        observer.assertValue(expectedResponse);
        observer.assertComplete();
        observer.assertNoErrors();
    }

    @SafeVarargs
    public static <O> void assertValues(Observable<Response<O>> operationStream, Response<O>... expectedResponses) {
        TestObserver<Response<O>> observer = operationStream.test();
        observer.awaitTerminalEvent();
        observer.assertValues(expectedResponses);
        observer.assertComplete();
        observer.assertNoErrors();
    }
}
