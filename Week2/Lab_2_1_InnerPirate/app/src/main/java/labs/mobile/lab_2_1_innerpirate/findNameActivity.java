package labs.mobile.lab_2_1_innerpirate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class findNameActivity extends AppCompatActivity
{
    private String m_PirateName;
    static final int CHOOSE_SHIP_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_name);
    }

    @Override
    protected  void onActivityResult(int pRequestCode, int pResultCode, Intent pData)
    {
        if(pResultCode == RESULT_OK && pRequestCode == CHOOSE_SHIP_REQUEST)
        {
            if(pData.hasExtra("ship"))
            {
                Intent data = new Intent();
                data.putExtra("ship", pData.getStringExtra("ship"));
                data.putExtra("name", m_PirateName);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public void getNameButtonOnClick(View pView)
    {
        Spinner colourSpinner = (Spinner)findViewById(R.id.colourSpinner);
        RadioGroup motiveGroup = (RadioGroup)findViewById(R.id.motiveRadioGroup);
        int selectedRadioIndex = motiveGroup.indexOfChild(findViewById(motiveGroup.getCheckedRadioButtonId()));
        int nameIndex = colourSpinner.getSelectedItemPosition() + selectedRadioIndex;
        TextView pirateName = (TextView)findViewById(R.id.nameTextView);

        String[] pirateNames = getResources().getStringArray(R.array.pirateNames);

        pirateName.setText("Yer name be " + pirateNames[nameIndex]);

        m_PirateName = pirateNames[nameIndex];

        Button findShipButton = (Button)findViewById(R.id.findShipButton);
        findShipButton.setEnabled(true);
    }

    public void findShipButtonOnClick(View pView)
    {
        Intent intent = new Intent(this, chooseShipActivity.class);
        intent.putExtra("pirateName", m_PirateName);
        startActivityForResult(intent, CHOOSE_SHIP_REQUEST);
    }

    public void setSailAloneButtonOnClick(View pView)
    {

    }
}
