package digital.bakehouse.rxusecase.operation;

import digital.bakehouse.rxusecase.Failure;

/**
 * Abstraction for continuous operations.
 * Useful in the process of converting stream-like implementation
 * into use-cases.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public interface Continuous<I, O> {

    /**
     * Entry point for the logic/action/mechanism this operation
     * represents.
     * The notifier interface is the proxy for passing data
     * resulted in and emitted by this operation to invokers of
     * this method
     *
     * @param input    Operation input
     * @param notifier Callback interface
     */
    void act(I input, Notifier<O> notifier);

    /**
     * Cancel/stop/clean-up any activities/resources started and or
     * allocated when {@link #act(Object, Notifier)} method was invoked,
     * or while it was executing
     *
     * @param input Operation input. As a rule this should be equal the input
     *              parameter passed to the {@link #act(Object, Notifier)} method
     */
    void cancel(I input);

    interface Notifier<O> {
        void notify(O output);

        void complete();

        void complete(Failure failure);
    }
}
