package digital.bakehouse.rxusecase;

import io.reactivex.functions.Consumer;

/**
 * This is a {@link Consumer} implementation for emissions of {@link Response} classes.
 * It simplifies differentiated handling of response scenarios applying a mechanism
 * to gracefully consume response success and response failures.
 * <p>
 * See {@link Response#isSuccessful()} for reference.
 *
 * @param <O> Output type
 */
public class ResponseConsumer<O> implements Consumer<Response<O>> {

    private Consumer<O> successConsumer;
    private Consumer<Failure> failureConsumer;

    /**
     * Create a consumer which handles only success cases
     *
     * @param successConsumer Delegate consumer
     */
    public ResponseConsumer(Consumer<O> successConsumer) {
        this.successConsumer = successConsumer;
    }

    /**
     * Create a consumer to handle success and failure
     * cases of {@link Response} class
     *
     * @param successConsumer Delegate success consumer
     * @param failureConsumer Delegate failure consumer
     */
    public ResponseConsumer(Consumer<O> successConsumer,
                            Consumer<Failure> failureConsumer) {
        this.successConsumer = successConsumer;
        this.failureConsumer = failureConsumer;
    }

    @Override
    public void accept(Response<O> response) throws Exception {
        if (response.isSuccessful()) {
            if (successConsumer != null) {
                successConsumer.accept(response.getData());
            }
        } else {
            if (failureConsumer != null) {
                failureConsumer.accept(response.getFailure());
            }
        }
    }

    /**
     * Factory method for consuming {@link Response} success.
     * Failure will be ignored.
     *
     * @param successConsumer Delegate success consumer
     * @param <O>             Output type
     * @return Response consumer
     */
    public static <O> ResponseConsumer<O> consumeSuccess(Consumer<O> successConsumer) {
        return new ResponseConsumer<>(successConsumer);
    }

    /**
     * Factory method for consuming {@link Response} failure.
     * Success will be ignored.
     *
     * @param failureConsumer Delegate failure consumer
     * @param <O>             Output type
     * @return Response consumer
     */
    public static <O> ResponseConsumer<O> consumeFailure(Consumer<Failure> failureConsumer) {
        return new ResponseConsumer<>(null, failureConsumer);
    }

    /**
     * Factory method for consuming {@link Response} success and failures.
     *
     * @param successConsumer Delegate success consumer
     * @param failureConsumer Delegate failure consumer
     * @param <O>             Output type
     * @return Response consumer
     */
    public static <O> ResponseConsumer<O> consume(Consumer<O> successConsumer,
                                                  Consumer<Failure> failureConsumer) {
        return new ResponseConsumer<>(successConsumer, failureConsumer);
    }
}
