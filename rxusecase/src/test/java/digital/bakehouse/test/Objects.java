package digital.bakehouse.test;

import java.util.Random;

public class Objects {

    private static final int DEFAULT_MAX = 100;

    public static int randomPositiveInt(int max) {
        return new Random().nextInt(max);
    }

    public static int randomPositiveInt() {
        return new Random().nextInt(DEFAULT_MAX);
    }
}
