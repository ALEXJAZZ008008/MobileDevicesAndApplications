package mobile.labs.acw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GoToInternetCheck();
    }

    private void GoToInternetCheck()
    {
        try
        {
            new InternetCheck(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void GoToMenuActivity()
    {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

        finish();
    }
}