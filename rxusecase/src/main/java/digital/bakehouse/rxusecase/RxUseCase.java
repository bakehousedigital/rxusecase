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
import digital.bakehouse.rxusecase.toolbox.Objects;
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
     * Create use-case observable with input.
     * This method will get called by invoking any of the:
     * {@link #create()}, {@link #create(Object)}, {@link #create(Request)} methods.
     * Implement it to define the logic/mechanism/action that this use-case represents.
     *
     * @param input Use-case input
     * @return Observable stream
     */
    protected abstract Observable<Response<O>> execute(I input);

    /**
     * Emit the passed object wrapped into a {@link Response}.
     * Utility method that builds and returns an {@link Observable}
     * that emits a successful {@link Response} wrapping the output.
     *
     * @param output Use-case output
     * @return Observable stream
     */
    protected Observable<Response<O>> justSucceed(O output) {
        return just(Response.succeed(output));
    }

    /**
     * Emit the passed code and message wrapped into a {@link Response}.
     * Utility method that builds and returns an {@link Observable}
     * that emits a failure {@link Response} wrapping the passed
     * error code and error message
     *
     * @param code    Error code
     * @param message Error message
     * @return Observable stream
     */
    protected Observable<Response<O>> justFail(String code, String message) {
        return just(Response.fail(code, message));
    }

    /**
     * Decorate this use-case with a list of {@link UseCaseDecorator} implementations.
     * Effectively, the use-case observable returned from {@link #execute(Object)} method
     * will be decorated in a chain manner with items in this list.
     * The order of elements in the passed list is important because the decorators
     * may decide to pre and post-process streams, for ex. by filtering out the emission
     * of certain elements.
     * NOTE: Invoking this method will disable the global decorators for this instance
     * of this use-case.
     * <p>
     * See {@link UseCaseDecorator} for reference
     *
     * @param decorators List of decorators
     * @param <T>        Type of this use-case
     * @return This use-case
     */
    @SuppressWarnings("unchecked")
    public final <T extends RxUseCase<I, O>> T decorateWith(Collection<UseCaseDecorator> decorators) {
        Objects.requireNonNull(decorators, "Decorators should not be null!");
        createDecorators().addAll(decorators);
        return (T) this;
    }

    /**
     * Add a {@link UseCaseDecorator} to the list of items decorating this use-case.
     * NOTE: Invoking this method will disable the global decorators for this instance
     * of this use-case.
     * Please see {@link #decorateWith(Collection)} and {@link UseCaseDecorator}
     * for reference
     *
     * @param decorator Decorator of use-case
     * @param <T>       Type of this use-case
     * @return This use-case
     */
    @SuppressWarnings("unchecked")
    public final <T extends RxUseCase<I, O>> T decorateWith(UseCaseDecorator decorator) {
        Objects.requireNonNull(decorator, "Decorator should not be null!");
        createDecorators().add(decorator);
        return (T) this;
    }

    /**
     * Remove all the decorators previously set for this use-case including
     * the global ones.
     *
     * @param <T> Type of this use-case
     * @return This use-case
     */
    @SuppressWarnings("unchecked")
    public final <T extends RxUseCase<I, O>> T decorateWithNothing() {
        createDecorators(true);
        return (T) this;
    }

    private Collection<UseCaseDecorator> getDecorators() {
        if (decorators != null) {
            return decorators;
        }
        return GLOBAL_DECORATORS;
    }

    private Collection<UseCaseDecorator> createDecorators() {
        return createDecorators(false);
    }

    private Collection<UseCaseDecorator> createDecorators(boolean forceNewInstance) {
        if (decorators == null || forceNewInstance) {
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

    /**
     * Add a global decorator for use-cases.
     * All use-case observables returned from their respective {@link #execute(Object)} method
     * will be decorated by this item, unless they define local {@link UseCaseDecorator}
     * or clear the decoration mechanism, see {@link #decorateWith(Collection)},
     * {@link #decorateWith(UseCaseDecorator)} and {@link #decorateWithNothing()} for this.
     *
     * @param decorator Global decorator
     */
    public static void addDecorator(UseCaseDecorator decorator) {
        Objects.requireNonNull(decorator, "Decorator should not be null!");
        GLOBAL_DECORATORS.add(decorator);
    }

    /**
     * Remove a global decorator of use-cases.
     *
     * @param decorator Global decorator
     */
    public static void removeDecorator(UseCaseDecorator decorator) {
        Objects.requireNonNull(decorator, "Decorator should not be null!");
        GLOBAL_DECORATORS.remove(decorator);
    }

    /**
     * Create a use-case from a {@link Synchronous} operation.
     * This method will wrap the logic / action / mechanism defined
     * in the operation into an instance of {@link RxUseCase}.
     * <p>
     * See {@link Synchronous} for reference.
     *
     * @param operation Use-case operation
     * @param <I>       Input type
     * @param <O>       Output type
     * @return Rx use-case
     */
    public static <I, O> RxUseCase<I, O> fromSynchronous(Synchronous<I, O> operation) {
        return fromSource(input -> toRx(operation, input));
    }

    /**
     * Create a use-case from a {@link Asynchronous} operation.
     * This method will wrap the logic / action / mechanism defined
     * in the operation into an instance of {@link RxUseCase}.
     * <p>
     * See {@link Asynchronous} for reference.
     *
     * @param operation Use-case operation
     * @param <I>       Input type
     * @param <O>       Output type
     * @return Rx use-case
     */
    public static <I, O> RxUseCase<I, O> fromAsynchronous(Asynchronous<I, O> operation) {
        return fromSource(input -> toRx(operation, input));
    }

    /**
     * Create a use-case from a {@link Continuous} operation.
     * This method will wrap the logic / action / mechanism defined
     * in the operation into an instance of {@link RxUseCase}.
     * <p>
     * See {@link Continuous} for reference.
     *
     * @param operation Use-case operation
     * @param <I>       Input type
     * @param <O>       Output type
     * @return Rx use-case
     */
    public static <I, O> RxUseCase<I, O> fromContinuous(Continuous<I, O> operation) {
        return fromSource(input -> toRx(operation, input));
    }

    /**
     * Create a use-case from an implementation of {@link RxSource}.
     * This method will wrap the logic / action / mechanism defined
     * in the source into an instance of {@link RxUseCase}.
     * It is useful for situations when extending {@link RxUseCase} class
     * is not an option, and the other operations are not suitable.
     * <p>
     * See {@link RxSource} for reference.
     *
     * @param source Observable source
     * @param <I>    Input type
     * @param <O>    Output type
     * @return Rx use-case
     */
    public static <I, O> RxUseCase<I, O> fromSource(RxSource<I, O> source) {
        return new DelegateUseCase<>(source);
    }

    /**
     * Transform a {@link Synchronous} operation into an {@link Observable}.
     *
     * @param operation Use-case operation
     * @param input     Operation input
     * @param <I>       Input type
     * @param <O>       Output type
     * @return Observable stream
     */
    protected static <I, O> Observable<Response<O>> toRx(Synchronous<I, O> operation,
                                                         I input) {
        return fromCallable(() -> operation.act(input))
                .map(Response::succeed);
    }

    /**
     * Transform an {@link Asynchronous} operation into an {@link Observable}.
     *
     * @param operation Use-case operation
     * @param input     Operation input
     * @param <I>       Input type
     * @param <O>       Output type
     * @return Observable stream
     */
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

    /**
     * Transform a {@link Continuous} operation into an {@link Observable}.
     *
     * @param operation Use-case operation
     * @param input     Operation input
     * @param <I>       Input type
     * @param <O>       Output type
     * @return Observable stream
     */
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

    /**
     * Create an {@link Observable} using a {@link SafeEmitter}.
     * This will allow invocations of {@link ObservableEmitter#onNext(Object)},
     * {@link ObservableEmitter#onNext(Object)} and {@link ObservableEmitter#onNext(Object)}
     * without first verifying the {@link ObservableEmitter#isDisposed()} status.
     * <p>
     * See {@link SafeEmitter} for reference.
     *
     * @param subscriber OnSubscribe hook
     * @param <O>        Output type
     * @return Observable stream
     */
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
