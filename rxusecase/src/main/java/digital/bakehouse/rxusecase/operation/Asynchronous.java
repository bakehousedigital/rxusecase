package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Failure;

public interface Asynchronous<I, O> {
    void act(I input, Callback<O> callback);

    interface Callback<O> {
        void succeed(O output);

        void fail(Failure failure);
    }
}
