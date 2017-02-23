package labs.mobile.lab_3_1_piratejokes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class punchlineActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punchline);

        int position = getIntent().getIntExtra("position", -1);

        TextView textView = (TextView)findViewById(R.id.punchlineTextView);

        String punchline = getResources().getStringArray(R.array.punchlines)[position];

        textView.setText(punchline);
    }
}
