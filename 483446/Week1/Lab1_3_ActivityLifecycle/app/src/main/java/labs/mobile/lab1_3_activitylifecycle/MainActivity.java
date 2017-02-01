package labs.mobile.lab1_3_activitylifecycle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Activity Lifecycle", "onCreate Method Called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Activity Lifecycle", "onStart Method Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Activity Lifecycle", "onResume Method Called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Activity Lifecycle", "onRestart Method Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Activity Lifecycle", "onStop Method Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Activity Lifecycle", "onDestroy Method Called");
    }
}
