package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Failure;

/**
 * Abstraction for asynchronous operations.
 * Useful in the process of converting callback-like implementation
 * into use-cases.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public interface Asynchronous<I, O> {

    /**
     * Entry point for the logic/action/mechanism this operation
     * represents.
     *
     * @param input    Operation input
     * @param callback Callback interface
     */
    void act(I input, Callback<O> callback);

    interface Callback<O> {
        void succeed(O output);

        void fail(Failure failure);
    }
}
