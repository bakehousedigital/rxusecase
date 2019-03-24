package digital.bakehouse.rxusecase.toolbox;

import java.util.Arrays;

public final class Objects {

    private Objects() {
    }

    public static boolean equals(Object var0, Object var1) {
        return var0 == var1 || var0 != null && var0.equals(var1);
    }

    public static int hashCode(Object var0) {
        return var0 != null ? var0.hashCode() : 0;
    }

    public static int hash(Object... var0) {
        return Arrays.hashCode(var0);
    }

    public static <T> T requireNonNull(T var0, String var1) {
        if (var0 == null) {
            throw new NullPointerException(var1);
        } else {
            return var0;
        }
    }
}
