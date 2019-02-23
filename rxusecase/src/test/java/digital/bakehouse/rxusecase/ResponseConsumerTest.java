package digital.bakehouse.rxusecase;

import org.junit.Test;

import io.reactivex.functions.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ResponseConsumerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void acceptsSuccess() throws Exception {
        Consumer<String> successConsumer = mock(Consumer.class);
        Consumer<Failure> failureConsumer = mock(Consumer.class);

        ResponseConsumer<String> consumer = new ResponseConsumer<>(
                successConsumer, failureConsumer
        );
        String input = "abcdefg";
        consumer.accept(Response.succeed(input));

        verify(successConsumer, times(1)).accept(input);
        verify(failureConsumer, times(0)).accept(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void acceptsFailure() throws Exception {
        Consumer<String> successConsumer = mock(Consumer.class);
        Consumer<Failure> failureConsumer = mock(Consumer.class);

        ResponseConsumer<String> consumer = new ResponseConsumer<>(
                successConsumer, failureConsumer
        );
        Failure failure = new Failure("1", "Failure message");
        consumer.accept(Response.fail(failure));

        verify(successConsumer, times(0)).accept(any());
        verify(failureConsumer, times(1)).accept(failure);
    }
}