package digital.bakehouse.rxusecase;

import io.reactivex.functions.Consumer;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ResponseConsumer<O> implements Consumer<Response<O>> {

    private Consumer<O> successConsumer;
    private Consumer<Failure> failureConsumer;

    public ResponseConsumer(Consumer<O> successConsumer) {
        this.successConsumer = successConsumer;
    }

    public ResponseConsumer(Consumer<O> successConsumer,
                            Consumer<Failure> failureConsumer) {
        this.successConsumer = successConsumer;
        this.failureConsumer = failureConsumer;
    }

    @Override
    public void accept(Response<O> response) throws Exception {
        if (response.isSuccessful()) {
            if (successConsumer != null) {
                successConsumer.accept(response.getData());
            }
        } else {
            if (failureConsumer != null) {
                failureConsumer.accept(response.getFailure());
            }
        }
    }

    public static <O> ResponseConsumer<O> consumeSuccess(Consumer<O> successConsumer) {
        return new ResponseConsumer<>(successConsumer);
    }

    public static <O> ResponseConsumer<O> consumeFailure(Consumer<Failure> failureConsumer) {
        return new ResponseConsumer<>(null, failureConsumer);
    }

    public static <O> ResponseConsumer<O> consume(Consumer<O> successConsumer,
                                                  Consumer<Failure> failureConsumer) {
        return new ResponseConsumer<>(successConsumer, failureConsumer);
    }
}
