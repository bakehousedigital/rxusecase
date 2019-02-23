package digital.bakehouse.rxusecase;

import digital.bakehouse.rxusecase.toolbox.Objects;

@SuppressWarnings("WeakerAccess")
public class Request<I> {
    private I input;
    private Object tag;
    private String origin;

    private Request(Builder<I> builder) {
        input = builder.input;
        tag = builder.tag;
    }

    public I getInput() {
        return input;
    }

    public Object getTag() {
        return tag;
    }

    Request<I> origin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request<?> request = (Request<?>) o;
        return Objects.equals(input, request.input) &&
                Objects.equals(tag, request.tag) &&
                Objects.equals(origin, request.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, tag, origin);
    }

    @Override
    public String toString() {
        return "Request{" +
                "input=" + input +
                ", tag=" + tag +
                ", origin='" + origin + '\'' +
                '}';
    }

    public static <I> Builder<I> newBuilder(I input) {
        return new Builder<>(input);
    }

    public static class Builder<I> {
        private I input;
        private Object tag;

        Builder(I input) {
            this.input = input;
        }

        public Builder<I> tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Request<I> build() {
            return new Request<>(this);
        }
    }

}
