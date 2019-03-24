package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Decorator taking care of applying thread specifications
 * to the use-case {@link Observable}s.
 * Useful especially when a threading strategy is defined
 * globally for all the use-cases.
 */
public final class SchedulerDecorator implements UseCaseDecorator {

    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;

    private SchedulerDecorator(Scheduler subscribeScheduler, Scheduler observeScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observeScheduler = observeScheduler;
    }

    /**
     * Factory method to create a decorator using the passed
     * scheduler for all the use-case operations.
     *
     * @param scheduler Operation scheduler
     * @return Decorator
     */
    public static SchedulerDecorator allOn(Scheduler scheduler) {
        return scheduleOn(scheduler, scheduler);
    }

    /**
     * Factory method to create a decorator using the passed
     * scheduler for use-case execution operations.
     *
     * @param scheduler Operation scheduler
     * @return Decorator
     */
    public static SchedulerDecorator subscribeOn(Scheduler scheduler) {
        return scheduleOn(scheduler, null);
    }

    /**
     * Factory method to create a decorator using the passed
     * scheduler for use-case notifying operations.
     *
     * @param scheduler Operation scheduler
     * @return Decorator
     */
    public static SchedulerDecorator observeOn(Scheduler scheduler) {
        return scheduleOn(null, scheduler);
    }

    /**
     * Factory method to create a decorator using the passed
     * schedulers for use-case execution and notifying operations.
     *
     * @param subscribeScheduler Execution scheduler
     * @param observeScheduler   Notifying scheduler
     * @return Decorator
     */
    public static SchedulerDecorator scheduleOn(Scheduler subscribeScheduler,
                                                Scheduler observeScheduler) {
        return new SchedulerDecorator(subscribeScheduler, observeScheduler);
    }

    @Override
    public final <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                         Request<I> request) {
        if (subscribeScheduler != null) {
            origin = origin.subscribeOn(subscribeScheduler);
        }

        if (observeScheduler != null) {
            origin = origin.observeOn(observeScheduler);
        }

        return origin;
    }
}
