package digital.bakehouse.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static digital.bakehouse.test.Objects.randomPositiveInt;
import static org.mockito.Mockito.mock;

public class Mocks {

    public static <T> Collection<T> mockCollection(Class<T> itemType) {
        return mockCollection(itemType, null);
    }

    public static <T> Collection<T> mockCollection(Class<T> itemType, Hook<T> hook) {
        return mockCollection(itemType, hook, randomPositiveInt());
    }

    public static <T> Collection<T> mockCollection(Class<T> itemType, Hook<T> hook, int size) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T mock = mock(itemType);
            if (hook != null) {
                hook.apply(mock);
            }

            result.add(mock);
        }

        return result;
    }
}
