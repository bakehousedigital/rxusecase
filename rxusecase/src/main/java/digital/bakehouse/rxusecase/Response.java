package digital.bakehouse.rxusecase;

import digital.bakehouse.rxusecase.toolbox.Objects;

/**
 * Wrapper of success and failure data.
 * Instances of this class are emitted by use-cases as outputs
 * for the action/logic/mechanism they wrap/define.
 *
 * @param <O> Output type
 */
public class Response<O> {
    private O data;
    private Failure failure;

    /**
     * Wrap success data and failure object
     *
     * @param data    Success
     * @param failure Failure
     */
    public Response(O data, Failure failure) {
        this.data = data;
        this.failure = failure;
    }

    public O getData() {
        return data;
    }

    public Failure getFailure() {
        return failure;
    }

    /**
     * Verify whether this {@link Response} is a wrapper of
     * success or failure data.
     *
     * @return true if success holder, false if failure holder
     */
    public boolean isSuccessful() {
        return failure == null;
    }

    /**
     * Factory method to wrap success data into a {@link Response} object.
     *
     * @param output Success data
     * @param <O>    Output type
     * @return Response object
     */
    public static <O> Response<O> succeed(O output) {
        return new Response<>(output, null);
    }

    /**
     * Factory method to wrap a failure into a {@link Response} object.
     *
     * @param failure Failure data
     * @param <O>     Output type
     * @return Response object
     */
    public static <O> Response<O> fail(Failure failure) {
        return new Response<>(null, failure);
    }

    /**
     * Factory method to wrap the passed code and message into
     * a failure {@link Response} object.
     *
     * @param errorCode    Failure code
     * @param errorMessage Failure message
     * @param <O>          Output type
     * @return Response object
     */
    public static <O> Response<O> fail(String errorCode, String errorMessage) {
        return fail(new Failure(errorCode, errorMessage));
    }

    /**
     * Factory method that builds a {@link O} typed failure @link Response} object,
     * from the {@link Response#failure} of passed input
     *
     * @param response Failure data holder
     * @param <O>      Output type
     * @return Response object
     */
    public static <O> Response<O> fail(Response<?> response) {
        return fail(response.failure);
    }

    /**
     * Factory method to create a {@link Void} wrapped success {@link Response} object.
     *
     * @return Response object
     */
    public static Response<Void> empty() {
        return succeed(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Response<?> response = (Response<?>) o;
        return Objects.equals(data, response.data) &&
                Objects.equals(failure, response.failure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, failure);
    }

    @Override
    public String toString() {
        return "Response{" +
                "data=" + data +
                ", failure=" + failure +
                '}';
    }
}
