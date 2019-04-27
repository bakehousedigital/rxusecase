package digital.bakehouse.rxusecase.purejava;

import android.annotation.SuppressLint;

import digital.bakehouse.rxusecase.RxUseCase;
import digital.bakehouse.rxusecase.decorator.LogDecorator;
import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.operation.Asynchronous;
import digital.bakehouse.rxusecase.operation.Continuous;

public class FromPlayground {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public static void main(String[] args) {
        //Sample local decorator which will be set on each created
        //use-case below. Generally for this it is easier to set it
        //globally by invoking the static addDecorator on RxUseCase
        UseCaseDecorator logDecorator = LogDecorator.getWithOutput(output -> System.out.println("Output: " + output),
                "-->");

        //Create a use-case from synchronous operation
        //and tag it with a string to better identify its output in logs
        RxUseCase
                .fromSynchronous(input -> System.currentTimeMillis())
                .origin("SynchronousGetTime")
                .decorateWith(logDecorator)
                .create()
                .subscribe(item -> System.out.println("From synchronous " + item));

        RxUseCase
                .fromAsynchronous((Asynchronous<Void, Long>) (input, callback)
                        -> callback.succeed(System.currentTimeMillis()))
                .origin("AsynchronousGetTime")
                .decorateWith(logDecorator)
                .create()
                .subscribe(item -> System.out.println("From asynchronous " + item));

        RxUseCase
                .fromContinuous(new Continuous<Void, Long>() {
                    @Override
                    public void act(Void input, Notifier<Long> notifier) {
                        notifier.notify(System.currentTimeMillis());
                        notifier.complete();
                    }

                    @Override
                    public void cancel(Void input) {
                        //do nothing
                    }
                })
                .origin("ContinuousGetTime")
                .decorateWith(logDecorator)
                .create()
                .subscribe(item -> System.out.println("From continuous" + item));

    }
}
