package digital.bakehouse.rxusecase;

import android.annotation.SuppressLint;

import digital.bakehouse.rxusecase.decorator.LogDecorator;
import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.operation.Asynchronous;
import io.reactivex.Observable;

import static digital.bakehouse.rxusecase.ResponseConsumer.consumeSuccess;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainClass {

    @SuppressLint("CheckResult")
    public static void main(String[] args) {


        RxUseCase.addDecorator(new ThreadInterceptor());
        RxUseCase.addDecorator(LogDecorator.getDefault());

        LoginUser loginUser = new LoginUser();

        LoginUser.Credentials loginRequest =
                new LoginUser.Credentials("test", "asd");
        loginUser.create(loginRequest)
                .subscribe(new ResponseConsumer<>(
                        token -> {
                            System.out.println("Normal create token is " + token);
                        }
                ));

        Request<LoginUser.Credentials> wrapRequest =
                Request.newBuilder(loginRequest)
                        .tag("BackgroundThread")
                        .build();
        loginUser.create(wrapRequest)
                .subscribe(consumeSuccess(
                        token -> {
                            System.out.println("Request wrap create is " + token);
                        }
                ));

        RxUseCase
                .wrap((Asynchronous<Boolean, Boolean>) (input, callback) -> {
                    callback.succeed(!input);
                }, false)
                .subscribe(item -> System.out.println("Wrap create is " + item));


    }

    static void whatever() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        System.out.println("ZZZ, " + stackTrace[5].getClassName());
    }


    static class LoginUser extends RxUseCase<LoginUser.Credentials,
            String> {

        @Override
        protected Observable<Response<String>> execute(final Credentials input) {
            whatever();
            return Observable
                    .fromCallable(() -> {
                        if ("test".equals(input.username)) {
                            return "qweqweqwe";
                        }
                        throw new IllegalArgumentException("No such user");
                    })
                    .map(Response::succeed);
        }

        static class Credentials {
            private String username;
            private String password;

            public Credentials(String username, String password) {
                this.username = username;
                this.password = password;
            }

            @Override
            public String toString() {
                return "Credentials{" +
                        "username='" + username + '\'' +
                        ", password='" + password + '\'' +
                        '}';
            }
        }
    }

    static class ThreadInterceptor implements UseCaseDecorator {

        @Override
        public <I, O> Observable<Response<O>> decorate(Observable<Response<O>> origin,
                                                       Request<I> request) {
            System.out.println("Thread spec is " + request.getTag());
            return origin;
        }
    }

}
