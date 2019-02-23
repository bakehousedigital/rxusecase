package digital.bakehouse.rxusecase;

import java.util.List;

import digital.bakehouse.rxusecase.toolbox.Objects;

public class Failure {
    private String code;
    private String message;
    private List<Failure> children;

    public Failure(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Failure(String code, String message, List<Failure> children) {
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

    public List<Failure> getChildren() {
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
