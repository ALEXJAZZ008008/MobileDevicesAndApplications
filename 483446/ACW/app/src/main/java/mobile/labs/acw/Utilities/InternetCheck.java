package mobile.labs.acw.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;

import mobile.labs.acw.MainActivity;

public class InternetCheck extends AsyncTask<Void, Void, Boolean>
{
    private MainActivity mainActivity;

    public InternetCheck(Context context)
    {
        mainActivity = (MainActivity)context;
    }

    public Boolean doInBackground(Void... args)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            try
            {
                URL url = new URL("http://www.google.com/");

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "test");
                httpURLConnection.setRequestProperty("Connection", "close");
                httpURLConnection.setConnectTimeout(1000); // mTimeout is in seconds
                httpURLConnection.connect();

                return (httpURLConnection.getResponseCode() == 200);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    protected void onPostExecute(Boolean arg)
    {
        if(!arg)
        {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        mainActivity.GoToMenuActivity();
    }
}
