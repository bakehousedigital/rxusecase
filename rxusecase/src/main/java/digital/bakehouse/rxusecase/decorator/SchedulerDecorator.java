package digital.bakehouse.rxusecase.decorator;

import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

public final class SchedulerDecorator implements UseCaseDecorator {

    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;

    private SchedulerDecorator(Scheduler subscribeScheduler, Scheduler observeScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observeScheduler = observeScheduler;
    }

    public static SchedulerDecorator allOn(Scheduler scheduler) {
        return scheduleOn(scheduler, scheduler);
    }

    public static SchedulerDecorator subscribeOn(Scheduler scheduler) {
        return scheduleOn(scheduler, null);
    }

    public static SchedulerDecorator observeOn(Scheduler scheduler) {
        return scheduleOn(null, scheduler);
    }

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
