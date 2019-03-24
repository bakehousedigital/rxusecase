package digital.bakehouse.rxusecase;

import java.util.Collection;

import digital.bakehouse.rxusecase.toolbox.Objects;

/**
 * Wrapper of errors.
 * This class is used by the failure outputs of use-cases for
 * wrapping errors returned by the logic/action/mechanism they represent.
 */
public class Failure {
    private String code;
    private String message;
    private Collection<Failure> children;

    /**
     * Create a failure with code and message.
     *
     * @param code    Failure code
     * @param message Failure message
     */
    public Failure(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Create a failure with code, message and a list of sub-failures.
     * This is useful in case the failure object needs to wrap a list
     * of errors.
     * Ex. you need to return an object containing all the input errors
     * (validation errors) of the user registration form.
     *
     * @param code     Failure code
     * @param message  Failure message
     * @param children Failure list
     */
    public Failure(String code, String message, Collection<Failure> children) {
        this.code = code;
        this.message = message;
        this.children = children;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Collection<Failure> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Failure failure = (Failure) o;
        return Objects.equals(code, failure.code) &&
                Objects.equals(message, failure.message) &&
                Objects.equals(children, failure.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, children);
    }

    @Override
    public String toString() {
        return "Failure{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", children=" + children +
                '}';
    }
}
