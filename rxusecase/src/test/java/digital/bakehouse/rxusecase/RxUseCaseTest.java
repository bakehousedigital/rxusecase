package digital.bakehouse.rxusecase;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.Collection;

import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import io.reactivex.Observable;

import static digital.bakehouse.test.Mocks.mockCollection;
import static digital.bakehouse.test.Observables.assertValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class RxUseCaseTest {

    @Test
    public void createVoidInput() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        useCase.create();

        verify(useCase, times(1)).execute(null);
    }

    @Test
    public void createTypeInput() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        Object input = mock(Object.class);
        useCase.create(input);

        verify(useCase, times(1)).execute(input);
    }

    @Test
    public void createRequestInput() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        Request request = mock(Request.class);
        Object input = mock(Object.class);
        when(request.getInput()).thenReturn(input);
        useCase.create(request);

        verify(useCase, times(1)).execute(input);
    }

    @Test
    public void getVoidInput() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        Response response = mock(Response.class);
        when(useCase.execute(null)).thenReturn(Observable.just(response));

        Response returnedResponse = useCase.get();

        verify(useCase, times(1)).execute(null);
        assertEquals(response, returnedResponse);
    }

    @Test
    public void getTypeInput() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        Response response = mock(Response.class);
        Request request = mock(Request.class);
        Object input = mock(Object.class);
        when(request.getInput()).thenReturn(input);

        when(useCase.execute(request.getInput())).thenReturn(Observable.just(response));

        Response returnedResponse = useCase.get(request.getInput());

        verify(useCase, times(1)).execute(request.getInput());
        assertEquals(response, returnedResponse);
    }

    @Test
    public void getRequestInput() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);

        Response response = mock(Response.class);
        Request request = mock(Request.class);
        Object input = mock(Object.class);
        when(request.getInput()).thenReturn(input);

        when(useCase.execute(request.getInput())).thenReturn(Observable.just(response));

        Response returnedResponse = useCase.get(request);

        verify(useCase, times(1)).execute(request.getInput());
        assertEquals(response, returnedResponse);
    }

    @Test
    public void justSucceed() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        Object output = mock(Object.class);
        Response<Object> expectedResponse = Response.succeed(output);

        Observable<Response<Object>> justSuccess = useCase.justSucceed(output);
        assertValue(justSuccess, expectedResponse);
    }

    @Test
    public void justFail() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        String code = "some code";
        String message = "some message";
        Failure output = new Failure(code, message);

        Response<Object> expectedResponse = Response.fail(output);

        Observable<Response<Object>> justFail = useCase.justFail(code, message);
        assertValue(justFail, expectedResponse);
    }

    @Test
    public void origin() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);
        String origin = "some origin";

        //Check that it returns the same instance
        RxUseCase useCaseWithOrigin = useCase.origin(origin);
        assertEquals(useCase, useCaseWithOrigin);

        //Check that the origin is set on a request without an origin
        Object input = mock(Object.class);
        Request request = Request.newBuilder(input).build();

        useCase.create(request);
        assertEquals(origin, request.getOrigin());

        //Check that the origin is not set on a request with an origin
        Object inputTwo = mock(Object.class);
        Request requestTwo = Request.newBuilder(input)
                .build()
                .origin("some other origin");

        useCase.create(requestTwo);
        assertNotEquals(origin, requestTwo.getOrigin());
    }

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    @Test
    public void decorateWithCollection() {
        RxUseCase useCase = mock(RxUseCase.class, Mockito.CALLS_REAL_METHODS);

        Request request = mock(Request.class);
        Object input = mock(Object.class);
        when(request.getInput()).thenReturn(input);

        Observable<Response<Object>> stream = mock(Observable.class);
        when(useCase.execute(input)).thenReturn(stream);

        Collection<UseCaseDecorator> decorators = mockCollection(UseCaseDecorator.class,
                decorator -> {
                    when(decorator.decorate(any(), any())).thenAnswer(
                            (Answer<Observable>) invocation -> {
                                Object[] args = invocation.getArguments();
                                return (Observable) args[0];
                            });
                });

        useCase.decorateWith(decorators)
                .create(request)
                .subscribe();

        InOrder inOrder = Mockito.inOrder(decorators.toArray(new UseCaseDecorator[0]));
        for (UseCaseDecorator decorator : decorators) {
            inOrder.verify(decorator).decorate(any(), any());
        }
    }
}
