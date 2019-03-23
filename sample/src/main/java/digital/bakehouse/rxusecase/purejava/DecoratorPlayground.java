package digital.bakehouse.rxusecase.purejava;

import android.annotation.SuppressLint;

import digital.bakehouse.rxusecase.FailureException;
import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.RxUseCase;
import digital.bakehouse.rxusecase.decorator.LogDecorator;
import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.operation.SynchronousUseCase;
import io.reactivex.Observable;

import static digital.bakehouse.rxusecase.ResponseConsumer.consumeSuccess;

public class DecoratorPlayground {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public static void main(String[] args) {
        GetTime getTimeNoDecor = new GetTime();
        getTimeNoDecor.create()
                .subscribe(consumeSuccess(
                        time -> System.out.println("Time with no decor is: " + time)
                ));

        GetTime getTimeWithDecor = new GetTime();
        getTimeNoDecor
                .decorateWith(new UseCaseDecorator() {
                    @Override
                    public <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                                   Request<I> request) {
                        System.out.println("Decorating use case");
                        return origin;
                    }
                })
                .create()
                .subscribe(consumeSuccess(
                        time -> System.out.println("Time with decor is: " + time)
                ));

        RxUseCase
                .fromSynchronous(input -> System.currentTimeMillis())
                .decorateWith(
                        LogDecorator.getWithOutput(output -> System.out.println("Output: " + output))
                )
                .create()
                .subscribe(item -> System.out.println("From synchronous " + item));
    }

    private static class GetTime extends SynchronousUseCase<Void, Long> {

        @Override
        public Long act(Void input) throws FailureException {
            return System.currentTimeMillis();
        }
    }
}
