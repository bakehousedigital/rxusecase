package digital.bakehouse.rxusecase;

import digital.bakehouse.rxusecase.toolbox.Objects;

public class Response<O> {
    private O data;
    private Failure failure;

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

    public boolean isSuccessful() {
        return failure == null;
    }

    public static <O> Response<O> succeed(O output) {
        return new Response<>(output, null);
    }

    public static <O> Response<O> fail(Failure failure) {
        return new Response<>(null, failure);
    }

    public static <O> Response<O> fail(String errorCode, String errorMessage) {
        return fail(new Failure(errorCode, errorMessage));
    }

    public static <O> Response<O> fail(Response<?> response) {
        return fail(response.failure);
    }

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
