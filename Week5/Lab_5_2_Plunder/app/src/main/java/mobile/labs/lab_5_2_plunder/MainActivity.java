package mobile.labs.lab_5_2_plunder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButtonOnClick(View pView) {
        Intent intent = new Intent(this, PlunderingActivity.class);
        startActivity(intent);
    }
}
