package digital.bakehouse.rxusecase.toolbox;

import io.reactivex.ObservableEmitter;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;

public final class SafeEmitter<O> implements ObservableEmitter<O> {

    private ObservableEmitter<O> decoratedEmitter;

    public SafeEmitter(ObservableEmitter<O> decoratedEmitter) {
        this.decoratedEmitter = decoratedEmitter;
    }

    @Override
    public void setDisposable(Disposable d) {
        decoratedEmitter.setDisposable(d);
    }

    @Override
    public void setCancellable(Cancellable c) {
        decoratedEmitter.setCancellable(c);
    }

    @Override
    public boolean isDisposed() {
        return decoratedEmitter.isDisposed();
    }

    @Override
    @NonNull
    public ObservableEmitter<O> serialize() {
        return decoratedEmitter.serialize();
    }

    @Override
    public boolean tryOnError(Throwable t) {
        return decoratedEmitter.tryOnError(t);
    }

    @Override
    public void onNext(O value) {
        if (!decoratedEmitter.isDisposed()) {
            decoratedEmitter.onNext(value);
        }
    }

    @Override
    public void onError(Throwable error) {
        if (!decoratedEmitter.isDisposed()) {
            decoratedEmitter.onError(error);
        }
    }

    @Override
    public void onComplete() {
        if (!decoratedEmitter.isDisposed()) {
            decoratedEmitter.onComplete();
        }
    }
}
