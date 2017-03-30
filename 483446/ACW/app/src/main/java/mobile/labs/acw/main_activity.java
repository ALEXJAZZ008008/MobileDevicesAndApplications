package mobile.labs.acw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import mobile.labs.acw.menu.menu_activity;
import mobile.labs.acw.utilities.main_internet_check;

public class main_activity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //This checks for an internet connection
        GoToInternetCheck();
    }

    //This checks for an internet connection
    private void GoToInternetCheck()
    {
        try
        {
            new main_internet_check(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //This goes to the menu activity
    public void GoToMenuActivity()
    {
        Intent intent = new Intent(this, menu_activity.class);
        startActivity(intent);

        finish();
    }
}