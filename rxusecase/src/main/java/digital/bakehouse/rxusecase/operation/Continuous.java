package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Failure;

public interface Continuous<I, O> {
    void act(I input, Notifier<O> notifier);

    void cancel(I input);

    interface Notifier<O> {
        void notify(O output);

        void complete();

        void complete(Failure failure);
    }
}
