package digital.bakehouse.rxusecase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.operation.Asynchronous;
import digital.bakehouse.rxusecase.operation.Continuous;
import digital.bakehouse.rxusecase.operation.Synchronous;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class RxUseCaseTest {

    @Test
    public void fromSynchronousSuccess() {
        String input = "abcdefg";
        Response<String> responseOutput = Response.succeed(input.toUpperCase());
        Synchronous<String, String> operation = String::toUpperCase;

        Observable<Response<String>> operationStream = RxUseCase.fromSynchronous(operation)
                .create(input);

        assertValue(operationStream, responseOutput);
    }

    @Test
    public void fromSynchronousError() {
        String input = "abcdefg";
        Failure expectedFailure = new Failure("1", "Failure message");
        Response<String> responseOutput = Response.fail(expectedFailure);
        Synchronous<String, String> operation = operationInput -> {
            throw FailureException.create("1", "Failure message");
        };

        Observable<Response<String>> operationStream = RxUseCase.fromSynchronous(operation)
                .create(input);

        assertValue(operationStream, responseOutput);
    }

    @Test
    public void fromAsynchronousSuccess() {
        String input = "abcdefg";
        Response<String> responseOutput = Response.succeed(input.toUpperCase());
        Asynchronous<String, String> operation = (operationInput, callback) ->
                new Thread(() -> callback.succeed(operationInput.toUpperCase())).start();

        Observable<Response<String>> operationStream = RxUseCase.fromAsynchronous(operation)
                .create(input);

        assertValue(operationStream, responseOutput);
    }

    @Test
    public void fromAsynchronousError() {
        String input = "abcdefg";
        Failure expectedFailure = new Failure("1", "Failure message");
        Response<String> responseOutput = Response.fail(expectedFailure);
        Asynchronous<String, String> operation = (operationInput, callback) ->
                new Thread(() -> callback.fail(new Failure("1", "Failure message"))).start();

        Observable<Response<String>> operationStream = RxUseCase.fromAsynchronous(operation)
                .create(input);

        assertValue(operationStream, responseOutput);
    }

    @Test
    public void fromContinuousSuccess() {
        String input = "abcdefg";
        char[] chars = input.toCharArray();
        List<Response<String>> responses = new ArrayList<>();
        for (char letter : chars) {
            responses.add(Response.succeed(String.valueOf(letter)));
        }

        Continuous<String, String> operation = new Continuous<String, String>() {
            @Override
            public void act(String input, Notifier<String> notifier) {
                new Thread(() -> {
                    for (char letter : input.toCharArray()) {
                        notifier.notify(String.valueOf(letter));
                    }
                    notifier.complete();
                }).start();
            }

            @Override
            public void cancel(String input) {

            }
        };

        Observable<Response<String>> operationStream = RxUseCase.fromContinuous(operation)
                .create(input);

        assertValues(operationStream, responses.toArray(new Response[0]));
    }

    @Test
    public void fromContinuousError() {
        String input = "abcdefg";
        char[] chars = input.toCharArray();
        List<Response<String>> responses = new ArrayList<>();
        for (char letter : chars) {
            responses.add(Response.succeed(String.valueOf(letter)));
        }
        responses.add(Response.fail(new Failure("1", "Some error message")));

        Continuous<String, String> operation = new Continuous<String, String>() {
            @Override
            public void act(String input, Notifier<String> notifier) {
                new Thread(() -> {
                    for (char letter : input.toCharArray()) {
                        notifier.notify(String.valueOf(letter));
                    }
                    notifier.complete(new Failure("1", "Some error message"));
                }).start();
            }

            @Override
            public void cancel(String input) {

            }
        };

        Observable<Response<String>> operationStream = RxUseCase.fromContinuous(operation)
                .create(input);

        assertValues(operationStream, responses.toArray(new Response[0]));
    }

    @Test
    public void decorates() {
        String input = "abcdefg";
        UseCaseDecorator decorator = mock(UseCaseDecorator.class);
        when(decorator.decorate(any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RxUseCase.addDecorator(decorator);
        Observable<Response<String>> operationStream = new RxUseCase<String, String>() {

            @Override
            protected Observable<Response<String>> execute(String input) {
                return Observable.just(input.toUpperCase())
                        .map(Response::succeed);
            }
        }.create(input);

        TestObserver<Response<String>> observer = operationStream.test();
        observer.awaitTerminalEvent();

        verify(decorator, times(1)).decorate(any(), any());
    }

    @Test
    public void decoratesInCorrectOrder() {
        String input = "abcdefg";
        UseCaseDecorator firstDecorator = new UseCaseDecorator() {
            @Override
            public <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                           Request<I> request) {
                return origin.map(oResponse -> {
                    O data = oResponse.getData();
                    if (data instanceof String) {
                        data = (O) ((String) data).concat((String) data);
                        return Response.succeed(data);
                    }
                    return oResponse;
                });
            }
        };
        UseCaseDecorator secondDecorator = new UseCaseDecorator() {
            @Override
            public <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                           Request<I> request) {
                return origin.map(oResponse -> {
                    O data = oResponse.getData();
                    if (data instanceof String) {
                        data = (O) ((String) data).replaceAll("A", "Z");
                        return Response.succeed(data);
                    }
                    return oResponse;
                });
            }
        };


        RxUseCase.addDecorator(firstDecorator);
        RxUseCase.addDecorator(secondDecorator);

        Observable<Response<String>> operationStream = new RxUseCase<String, String>() {
            @Override
            protected Observable<Response<String>> execute(String input) {
                return Observable.just(input.toUpperCase())
                        .map(Response::succeed);
            }
        }.create(input);

        Response<String> item = operationStream.blockingFirst();
        assertEquals("ZBCDEFGZBCDEFG", item.getData());

        RxUseCase.removeDecorator(firstDecorator);
        RxUseCase.removeDecorator(secondDecorator);
    }

    private <O> void assertValue(Observable<Response<O>> operationStream, Response<O> expectedResponse) {
        TestObserver<Response<O>> observer = operationStream.test();
        observer.awaitTerminalEvent();
        observer.assertValue(expectedResponse);
        observer.assertComplete();
        observer.assertNoErrors();
    }

    @SafeVarargs
    private final <O> void assertValues(Observable<Response<O>> operationStream, Response<O>... expectedResponses) {
        TestObserver<Response<O>> observer = operationStream.test();
        observer.awaitTerminalEvent();
        observer.assertValues(expectedResponses);
        observer.assertComplete();
        observer.assertNoErrors();
    }
}