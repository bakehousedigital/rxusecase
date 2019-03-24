package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.FailureException;

/**
 * Abstraction for synchronous operations.
 * Useful in the process of converting one-shot-like implementation
 * into use-cases.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public interface Synchronous<I, O> {

    /**
     * Entry point for the logic/action/mechanism this operation
     * represents.
     *
     * @param input Operation input
     * @return Operation output
     * @throws FailureException Exception representing the logical error of this
     *                          operation execution
     */
    O act(I input) throws FailureException;
}
