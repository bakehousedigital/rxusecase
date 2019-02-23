package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.FailureException;

public interface Synchronous<I, O> {
    O act(I input) throws FailureException;
}
