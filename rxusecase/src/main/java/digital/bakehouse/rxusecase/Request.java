package digital.bakehouse.rxusecase;

import java.util.HashMap;
import java.util.Map;

import digital.bakehouse.rxusecase.toolbox.Objects;

public class Request<I> {
    private I input;
    private Object tag;
    private String origin;
    private Map<String, Object> extras;

    private Request(Builder<I> builder) {
        input = builder.input;
        tag = builder.tag;
        extras = builder.extras;
    }

    public I getInput() {
        return input;
    }

    public Object getTag() {
        return tag;
    }

    public Map<String, Object> getExtras() {
        return extras;
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
                Objects.equals(origin, request.origin) &&
                Objects.equals(extras, request.extras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, tag, origin, extras);
    }

    @Override
    public String toString() {
        return "Request{" +
                "input=" + input +
                ", tag=" + tag +
                ", origin='" + origin + '\'' +
                ", extras=" + extras +
                '}';
    }

    public static <I> Builder<I> newBuilder(I input) {
        return new Builder<>(input);
    }

    public static <I> Builder<I> newBuilder() {
        return new Builder<>(null);
    }

    public static class Builder<I> {
        private I input;
        private Object tag;
        private Map<String, Object> extras;

        Builder(I input) {
            this.input = input;
        }

        public Builder<I> tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder<I> extras(Map<String, Object> extras) {
            this.extras = extras;
            return this;
        }

        public Builder<I> extra(String key, Object value) {
            if (extras == null) {
                extras = new HashMap<>();
            }
            extras.put(key, value);
            return this;
        }

        public Request<I> build() {
            return new Request<>(this);
        }
    }
}
