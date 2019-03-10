package digital.bakehouse.rxusecase.purejava;

import android.annotation.SuppressLint;

import digital.bakehouse.rxusecase.FailureException;
import digital.bakehouse.rxusecase.Request;
import digital.bakehouse.rxusecase.Response;
import digital.bakehouse.rxusecase.ResponseConsumer;
import digital.bakehouse.rxusecase.RxUseCase;
import digital.bakehouse.rxusecase.decorator.LogDecorator;
import digital.bakehouse.rxusecase.decorator.UseCaseDecorator;
import digital.bakehouse.rxusecase.operation.Asynchronous;
import io.reactivex.Observable;

import static digital.bakehouse.rxusecase.ResponseConsumer.consumeSuccess;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LoginPlayground {

    @SuppressLint("CheckResult")
    public static void main(String[] args) {


        RxUseCase.addDecorator(new ThreadInterceptor());
        RxUseCase.addDecorator(LogDecorator.getDefault());

        loginSuccessfully();

        loginWrongCredentials();

        loginWithThreadSpec();
    }

    @SuppressLint("CheckResult")
    private static void loginSuccessfully() {
        LoginUser loginUser = new LoginUser();

        LoginUser.Credentials loginRequest =
                new LoginUser.Credentials("testUser@test.com", "mypass");
        loginUser.create(loginRequest)
                .subscribe(new ResponseConsumer<>(
                        token -> {
                            System.out.println("loginSuccessfully success: " + token);
                        },
                        failure -> {
                            System.out.println("loginSuccessfully failure: " + failure);
                        }
                ));
    }

    @SuppressLint("CheckResult")
    private static void loginWrongCredentials() {
        LoginUser loginUser = new LoginUser();

        LoginUser.Credentials loginRequest =
                new LoginUser.Credentials("wrongUser@test.com", "mypass");
        loginUser.create(loginRequest)
                .subscribe(new ResponseConsumer<>(
                        token -> {
                            System.out.println("loginWrongCredentials success: " + token);
                        },
                        failure -> {
                            System.out.println("loginWrongCredentials failure: " + failure);
                        }
                ));
    }

    @SuppressLint("CheckResult")
    private static void loginWithThreadSpec() {
        LoginUser loginUser = new LoginUser();

        LoginUser.Credentials loginRequest =
                new LoginUser.Credentials("testUser@test.com", "mypass");
        Request<LoginUser.Credentials> wrapRequest = Request
                .newBuilder(loginRequest)
                .tag("BackgroundThread")
                .build();

        loginUser.create(wrapRequest)
                .subscribe(new ResponseConsumer<>(
                        token -> {
                            System.out.println("loginWithThreadSpec success: " + token);
                        },
                        failure -> {
                            System.out.println("loginWithThreadSpec failure: " + failure);
                        }
                ));
    }

    static class LoginUser extends RxUseCase<LoginUser.Credentials,
            String> {

        @Override
        protected Observable<Response<String>> execute(final Credentials input) {
            return Observable
                    .fromCallable(() -> {
                        if ("testUser@test.com".equals(input.username)) {
                            return "test-session-token";
                        }
                        throw new FailureException("No such user");
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
