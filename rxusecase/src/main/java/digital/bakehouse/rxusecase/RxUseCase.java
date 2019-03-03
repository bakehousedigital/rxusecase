package digital.bakehouse.rxusecase;

import java.util.ArrayList;
import java.util.List;

import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.decorator.FailureExceptionConverter;
import digital.bakehouse.rxusecase.operation.Asynchronous;
import digital.bakehouse.rxusecase.operation.Continuous;
import digital.bakehouse.rxusecase.operation.Synchronous;
import digital.bakehouse.rxusecase.toolbox.SafeEmitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static io.reactivex.Observable.defer;
import static io.reactivex.Observable.fromCallable;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class RxUseCase<I, O> {
    private final String ORIGIN = getClass().getSimpleName();
    private static final List<UseCaseDecorator> DECORATORS = new ArrayList<>();

    static {
        DECORATORS.add(FailureExceptionConverter.getDefault());
    }

    public final Observable<Response<O>> create() {
        return create((I) null);
    }

    public final Observable<Response<O>> create(I input) {
        return create(wrapRequest(input));
    }

    public final Observable<Response<O>> create(Request<I> request) {
        return decorate(execute(request.getInput()), withOrigin(request, ORIGIN));
    }

    public final Response<O> get(I input) {
        return get(newRequest(input));
    }

    public final Response<O> get(Request<I> request) {
        return create(request).blockingFirst();
    }

    protected abstract Observable<Response<O>> execute(I input);

    private Request<I> wrapRequest(I input) {
        return Request.newBuilder(input).build();
    }

    private static <I> Request<I> newRequest(I input) {
        return withOrigin(Request.newBuilder(input).build(),
                RxUseCase.class.getSimpleName());
    }

    private static <I> Request<I> withOrigin(Request<I> request, String origin) {
        return request.origin(origin);
    }

    public static void addDecorator(UseCaseDecorator decorator) {
        DECORATORS.add(decorator);
    }

    public static void removeDecorator(UseCaseDecorator decorator) {
        DECORATORS.remove(decorator);
    }

    public static <I, O> Observable<Response<O>> toRx(Synchronous<I, O> operation,
                                                      I input) {
        return fromCallable(() -> operation.act(input))
                .map(Response::succeed);
    }

    public static <I, O> Observable<Response<O>> wrap(Synchronous<I, O> operation,
                                                      I input) {
        return wrap(operation, newRequest(input));
    }

    public static <I, O> Observable<Response<O>> wrap(Synchronous<I, O> operation,
                                                      Request<I> request) {
        return decorate(toRx(operation, request.getInput()), request);
    }

    public static <I, O> Observable<Response<O>> toRx(Asynchronous<I, O> operation,
                                                      I input) {
        return create(emitter ->
                operation.act(input, new Asynchronous.Callback<O>() {
                    @Override
                    public void succeed(O output) {
                        emitter.onNext(Response.succeed(output));
                        emitter.onComplete();
                    }

                    @Override
                    public void fail(Failure failure) {
                        emitter.onNext(Response.fail(failure));
                        emitter.onComplete();
                    }
                }));
    }

    public static <I, O> Observable<Response<O>> wrap(Asynchronous<I, O> operation,
                                                      I input) {
        return wrap(operation, newRequest(input));
    }

    public static <I, O> Observable<Response<O>> wrap(Asynchronous<I, O> operation,
                                                      Request<I> request) {
        return decorate(toRx(operation, request.getInput()), request);
    }

    public static <I, O> Observable<Response<O>> toRx(Continuous<I, O> operation,
                                                      I input) {
        return create((ObservableEmitter<Response<O>> emitter) ->
                operation.act(input, new Continuous.Notifier<O>() {
                    @Override
                    public void notify(O output) {
                        emitter.onNext(Response.succeed(output));
                    }

                    @Override
                    public void complete() {
                        emitter.onComplete();
                    }

                    @Override
                    public void complete(Failure failure) {
                        emitter.onNext(Response.fail(failure));
                        emitter.onComplete();
                    }
                }))
                .doOnDispose(() -> operation.cancel(input));
    }

    public static <I, O> Observable<Response<O>> wrap(Continuous<I, O> operation,
                                                      I input) {
        return wrap(operation, newRequest(input));
    }

    public static <I, O> Observable<Response<O>> wrap(Continuous<I, O> operation,
                                                      Request<I> request) {
        return decorate(toRx(operation, request.getInput()), request);
    }

    public static <I, O> Observable<Response<O>> wrap(Observable<O> stream,
                                                      I input) {
        return wrap(stream, newRequest(input));
    }

    public static <I, O> Observable<Response<O>> wrap(Observable<O> stream,
                                                      Request<I> request) {
        return decorate(stream.map(Response::succeed), request);
    }

    private static <I, O> Observable<Response<O>> decorate(Observable<Response<O>> stream,
                                                           Request<I> request) {
        return defer(() -> {
            Observable<Response<O>> result = stream;
            for (UseCaseDecorator decorator : DECORATORS) {
                result = decorator.decorate(result, request);
            }
            return result;
        });
    }

    private static <O> Observable<Response<O>> create(ObservableOnSubscribe<Response<O>> subscriber) {
        return Observable.create(emitter ->
                subscriber.subscribe(new SafeEmitter<>(emitter)));
    }

}
