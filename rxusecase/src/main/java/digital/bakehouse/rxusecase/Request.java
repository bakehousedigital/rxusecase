package digital.bakehouse.rxusecase;

import java.util.HashMap;
import java.util.Map;

import digital.bakehouse.rxusecase.toolbox.Objects;

/**
 * Data holder, acting as a wrapper of the input object.
 * Instances of this class are passed to use-case classes as inputs
 * for the action/logic/mechanism they wrap/define.
 * Additionally this class exposes ways of tagging and setting
 * extra parameters to requests which can be used by use-cases
 * and their decorators.
 *
 * @param <I> Input type
 */
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

    /**
     * Set the originator of the {@link Request}.
     * Useful for logging.
     *
     * @param origin Originator
     * @return This request
     */
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

    /**
     * Create a new {@link Builder} instance for building and configuring
     * the {@link Request} object.
     *
     * @param input Input object
     * @param <I>   Input type
     * @return Builder instance for creating the {@link Request}
     */
    public static <I> Builder<I> newBuilder(I input) {
        return new Builder<>(input);
    }

    /**
     * Create a new {@link Builder} instance for building and configuring
     * the {@link Request} object.
     * The {@link Request#input} object will be set to null.
     * Useful for use-cases which do not require any input.
     *
     * @param <I> Input type
     * @return Builder instance for creating the {@link Request}
     */
    public static <I> Builder<I> newBuilder() {
        return new Builder<>(null);
    }

    /**
     * Builder pattern for creating and configuring {@link Request} objects.
     *
     * @param <I> Input type
     */
    public static class Builder<I> {
        private I input;
        private Object tag;
        private Map<String, Object> extras;

        Builder(I input) {
            this.input = input;
        }

        /**
         * Tag the {@link Request} with the passed object.
         * Useful for better differentiation.
         *
         * @param tag Tag
         * @return This builder
         */
        public Builder<I> tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * Set the {@link Request} extra parameters.
         *
         * @param extras Extra parameters
         * @return This builder
         */
        public Builder<I> extras(Map<String, Object> extras) {
            this.extras = extras;
            return this;
        }

        /**
         * Add an extra parameter to the built {@link Request}
         *
         * @param key   Parameter key
         * @param value Parameter value
         * @return This builder
         */
        public Builder<I> extra(String key, Object value) {
            if (extras == null) {
                extras = new HashMap<>();
            }
            extras.put(key, value);
            return this;
        }

        /**
         * Build the {@link Request} configured with data
         * passed in the builder methods.
         *
         * @return Configured {@link Request} object
         */
        public Request<I> build() {
            return new Request<>(this);
        }
    }
}
