package labs.mobile.lab_2_1_innerpirate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class chooseShipActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ship);

        String pirateName = getIntent().getStringExtra("pirateName");
        TextView greeting = (TextView)findViewById(R.id.greetingTextView);
        greeting.setText("Choose ye ship, " + pirateName + "!");
    }

    public void chooseShipButtonOnClick(View pView)
    {
        Intent data = new Intent();
        Spinner shipSpinner = (Spinner)findViewById(R.id.shipSpinner);
        String ship = (String)shipSpinner.getSelectedItem();
        data.putExtra("ship", ship);
        setResult(RESULT_OK, data);
        finish();
    }
}
