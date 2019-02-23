package digital.bakehouse.rxusecase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import digital.bakehouse.rxusecase.decorator.LogDecorator;
import digital.bakehouse.rxusecase.decorator.SchedulerDecorator;
import digital.bakehouse.rxusecase.operation.SynchronousUseCase;
import digital.bakehouse.rxusecase.toolbox.None;
import digital.bakehouse.rxusecase.toolbox.Objects;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SampleActivity extends AppCompatActivity implements LogDecorator.LogOutput {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add logging
        RxUseCase.addDecorator(LogDecorator.getWithOutput(this));
        RxUseCase.addDecorator(SchedulerDecorator.allOn(Schedulers.io()));

        new GetTimeMillis()
                .create(None.VALUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> Toast.makeText(this,
                        String.valueOf(item), Toast.LENGTH_SHORT).show());

        new HashString()
                .create("Test Input")
                .subscribe();
    }

    @Override
    public void log(String message) {
        Log.d(getClass().getSimpleName(), Thread.currentThread().getName() + ": " + message);
    }

    static class GetTimeMillis extends SynchronousUseCase<None, Long> {

        @Override
        public Long act(None input) {
            return System.currentTimeMillis();
        }
    }

    static class HashString extends SynchronousUseCase<String, String> {

        @Override
        public String act(String input) {
            return String.valueOf(Objects.hash(input));
        }
    }

}
