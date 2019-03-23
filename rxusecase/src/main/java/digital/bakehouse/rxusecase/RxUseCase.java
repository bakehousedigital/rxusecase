package digital.bakehouse.rxusecase;

import java.util.ArrayList;
import java.util.Collection;

import digital.bakehouse.rxusecase.decorator.FailureExceptionConverter;
import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.operation.Asynchronous;
import digital.bakehouse.rxusecase.operation.Continuous;
import digital.bakehouse.rxusecase.operation.DelegateUseCase;
import digital.bakehouse.rxusecase.operation.RxSource;
import digital.bakehouse.rxusecase.operation.Synchronous;
import digital.bakehouse.rxusecase.toolbox.SafeEmitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static io.reactivex.Observable.defer;
import static io.reactivex.Observable.fromCallable;
import static io.reactivex.Observable.just;

/**
 * RxUseCase wraps the logic / action / mechanism that the use-case implements,
 * into an Observable that can be subscribed to.
 * It can act as an interactor, communicating to with internal or external
 * components or systems for:
 * - validating data, current state or context
 * - retrieving or setting data, local or remote
 * - subscribing to continuous data changes
 * - doing any sort of operations, long or short running, one-shot or continously emitting
 * It should be thought of as a self-contained operation with single contextual responsibility.
 * <p>
 * RxUseCase has a standard interface of passing and getting data. It:
 * - operates with the Request parameter, which wraps the input required by the use-case
 * - emits a Response object which encapsulates either a successful output
 * or the failure returned by the use-case
 * This enables another way of describing use-cases as entities which act
 * as a standard input - standard output proxies to the outside world.
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public abstract class RxUseCase<I, O> {
    private static final Collection<UseCaseDecorator> GLOBAL_DECORATORS = new ArrayList<>();

    static {
        addDefaultDecoratorsInto(GLOBAL_DECORATORS);
    }

    private final String ORIGIN = getClass().getSimpleName();
    private Collection<UseCaseDecorator> decorators;

    /**
     * Create use-case observable without passing any input.
     * This effectively will invoke {@link #create(Object)} passing null as input.
     * The sole purpose of this method is to simplify the use-case usage
     * when no input is required by the use-case.
     *
     * @return Observable stream
     */
    public final Observable<Response<O>> create() {
        return create((I) null);
    }

    /**
     * Create use-case observable with input.
     * The passed input will be wrapped into a {@link Request} object
     * and passed to the {@link #create(Request)} method.
     *
     * @param input Use-case input
     * @return Observable stream
     */
    public final Observable<Response<O>> create(I input) {
        return create(wrapRequest(input));
    }

    /**
     * Create use-case observable passing a {@link Request} parameter.
     * This method is the entry point to the logic/mechanism/action
     * that this use-case represents.
     * Typically the subscription to the returned {@link Observable}
     * will trigger the execution of the use-case.
     *
     * @param request Use-case request
     * @return Observable stream
     */
    public final Observable<Response<O>> create(Request<I> request) {
        return decorate(execute(request.getInput()),
                withOrigin(request, ORIGIN), getDecorators());
    }

    /**
     * Get use-case response.
     * This effectively will invoke {@link #get(Object)} passing null as input.
     * The sole purpose of this method is to simplify the use-case usage
     * when no input is required by the use-case.
     *
     * @return Use-case response.
     */
    public final Response<O> get() {
        return get((I) null);
    }


    /**
     * Get use-case response for the input.
     * The passed input will be wrapped into a {@link Request} object
     * and passed to the {@link #get(Request)} method.
     *
     * @param input Use-case input
     * @return Use-case response
     */
    public final Response<O> get(I input) {
        return get(newRequest(input));
    }

    /**
     * Get use-case response passing a {@link Request} parameter.
     * This method is pointing to the entry point to the logic/mechanism/action
     * that this use-case represents.
     * It is useful in situations when the response of the use-case
     * is required to be retrieved in a blocking manner.
     * The method will block until the first item is emitted by {@link #create(Request)},
     * return it. See {@link Observable#blockingFirst()} for reference.
     *
     * @param request Use-case request
     * @return Observable stream
     */
    public final Response<O> get(Request<I> request) {
        return create(request).blockingFirst();
    }

    /**
     * @param input Use-case input
     * @return Observable stream
     */
    protected abstract Observable<Response<O>> execute(I input);

    protected Observable<Response<O>> justSucceed(O output) {
        return just(Response.succeed(output));
    }

    protected Observable<Response<O>> justFail(String code, String message) {
        return just(Response.fail(code, message));
    }

    @SuppressWarnings("unchecked")
    public final <T extends RxUseCase<I, O>> T decorateWith(Collection<UseCaseDecorator> decorators) {
        createDecorators().addAll(decorators);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final <T extends RxUseCase<I, O>> T decorateWith(UseCaseDecorator decorator) {
        createDecorators().add(decorator);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final <T extends RxUseCase<I, O>> T decorateWithNothing() {
        createDecorators();
        return (T) this;
    }

    private Collection<UseCaseDecorator> getDecorators() {
        if (decorators != null) {
            return decorators;
        }
        return GLOBAL_DECORATORS;
    }

    private Collection<UseCaseDecorator> createDecorators() {
        if (decorators == null) {
            decorators = new ArrayList<>();
            addDefaultDecoratorsInto(decorators);
        }
        return decorators;
    }

    private static <I> Request<I> wrapRequest(I input) {
        return Request.newBuilder(input).build();
    }

    private static <I> Request<I> newRequest(I input) {
        return withOrigin(Request.newBuilder(input).build(),
                RxUseCase.class.getSimpleName());
    }

    private static <I> Request<I> withOrigin(Request<I> request, String origin) {
        return request.origin(origin);
    }

    private static void addDefaultDecoratorsInto(Collection<UseCaseDecorator> decorators) {
        decorators.add(FailureExceptionConverter.getDefault());
    }

    public static void addDecorator(UseCaseDecorator decorator) {
        GLOBAL_DECORATORS.add(decorator);
    }

    public static void removeDecorator(UseCaseDecorator decorator) {
        GLOBAL_DECORATORS.remove(decorator);
    }

    public static <I, O> RxUseCase<I, O> fromSynchronous(Synchronous<I, O> operation) {
        return fromSource(input -> toRx(operation, input));
    }

    public static <I, O> RxUseCase<I, O> fromAsynchronous(Asynchronous<I, O> operation) {
        return fromSource(input -> toRx(operation, input));
    }

    public static <I, O> RxUseCase<I, O> fromContinuous(Continuous<I, O> operation) {
        return fromSource(input -> toRx(operation, input));
    }

    public static <I, O> RxUseCase<I, O> fromSource(RxSource<I, O> source) {
        return new DelegateUseCase<>(source);
    }

    protected static <I, O> Observable<Response<O>> toRx(Synchronous<I, O> operation,
                                                         I input) {
        return fromCallable(() -> operation.act(input))
                .map(Response::succeed);
    }

    protected static <I, O> Observable<Response<O>> toRx(Asynchronous<I, O> operation,
                                                         I input) {
        return safeCreate(emitter ->
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

    protected static <I, O> Observable<Response<O>> toRx(Continuous<I, O> operation,
                                                         I input) {
        return safeCreate((ObservableEmitter<Response<O>> emitter) ->
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

    protected static <O> Observable<Response<O>> safeCreate(
            ObservableOnSubscribe<Response<O>> subscriber) {
        return Observable.create(emitter ->
                subscriber.subscribe(new SafeEmitter<>(emitter)));
    }

    private static <I, O> Observable<Response<O>> decorate(Observable<Response<O>> stream,
                                                           Request<I> request,
                                                           Collection<UseCaseDecorator> decorators) {
        return defer(() -> {
            Observable<Response<O>> result = stream;
            for (UseCaseDecorator decorator : decorators) {
                result = decorator.decorate(result, request);
            }
            return result;
        });
    }
}
