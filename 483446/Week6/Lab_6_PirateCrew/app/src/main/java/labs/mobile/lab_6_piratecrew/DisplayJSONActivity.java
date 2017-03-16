package labs.mobile.lab_6_piratecrew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayJSONActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_json);
        TextView textView = (TextView)findViewById(R.id.JSONTextView);
        textView.setText("Hello, JSON!");
    }
}
