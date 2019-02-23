package digital.bakehouse.rxusecase.toolbox;

public final class None {
    public static final None VALUE = new None();

    private None() {
    }

    @Override
    public String toString() {
        return "None";
    }
}
