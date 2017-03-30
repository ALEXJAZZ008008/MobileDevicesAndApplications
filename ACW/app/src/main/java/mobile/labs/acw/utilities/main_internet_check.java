package mobile.labs.acw.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;

import mobile.labs.acw.main_activity;
import mobile.labs.acw.R;

public class main_internet_check extends AsyncTask<Void, Void, Boolean>
{
    private main_activity mainActivity;

    public main_internet_check(Context context)
    {
        mainActivity = (main_activity)context;
    }

    public Boolean doInBackground(Void... args)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            try
            {
                //This pings google
                URL url = new URL("http://www.google.com/");

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "test");
                httpURLConnection.setRequestProperty("Connection", "close");
                httpURLConnection.setConnectTimeout(1000); // mTimeout is in seconds
                httpURLConnection.connect();

                //This returns the result of pinging google
                return (httpURLConnection.getResponseCode() == 200);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    protected void onPostExecute(Boolean argument)
    {
        if(!argument)
        {
            Toast.makeText(mainActivity, R.string.internet, Toast.LENGTH_SHORT).show();
        }

        mainActivity.GoToMenuActivity();
    }
}