package digital.bakehouse.test;

public interface Hook<T> {
    void apply(T input);
}
