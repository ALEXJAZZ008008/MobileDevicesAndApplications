package labs.mobile.lab_2_1_innerpirate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class findNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_name);
    }

    public void getNameButtonOnClick(View pView) {
        Spinner colourSpinner = (Spinner)findViewById(R.id.colourSpinner);
        RadioGroup motiveGroup = (RadioGroup)findViewById(R.id.motiveRadioGroup);
        int selectedRadioIndex = motiveGroup.indexOfChild(findViewById(motiveGroup.getCheckedRadioButtonId()));
        int nameIndex = colourSpinner.getSelectedItemPosition() + selectedRadioIndex;
        TextView pirateName = (TextView)findViewById(R.id.nameTextView);

        String[] pirateNames = getResources().getStringArray(R.array.pirateNames);

        pirateName.setText("Yer name be " + pirateNames[nameIndex]);
    }

    public void findShipButtonOnClick(View pView) {

    }

    public void setSailAloneButtonOnClick(View pView) {

    }
}
