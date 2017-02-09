package labs.mobile.lab_2_1_innerpirate;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private String m_Result;
    static final int CHOOSE_NAME_AND_SHIP_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected  void onActivityResult(int pRequestCode, int pResultCode, Intent pData)
    {
        if(pResultCode == RESULT_OK && pRequestCode == CHOOSE_NAME_AND_SHIP_REQUEST)
        {
            if(pData.hasExtra("name") && pData.hasExtra("ship"))
            {
                TextView pirateName = (TextView)findViewById(R.id.pirateNameTextView);
                String result = "Arr, 'tis " + pData.getStringExtra("name") + " of the " +
                                pData.getStringExtra("ship");
                pirateName.setText(result);
                m_Result = result;

                Button shareButton = (Button)findViewById(R.id.shareButton);
                shareButton.setEnabled(true);
            }
        }
    }
	
	public void findNameButtonOnClick(View pView)
    {
        Intent intent = new Intent(this, findNameActivity.class);
        startActivityForResult(intent, CHOOSE_NAME_AND_SHIP_REQUEST);
    }

    public void shareButtonOnClick(View pView)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:555555"));
        intent.putExtra("sms_body", m_Result);
        startActivity(intent);
    }	
}
