package mobile.labs.acw.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;
import mobile.labs.acw.R;
import mobile.labs.acw.menu.menu_activity;

public class download_internet_check extends AsyncTask<Void, Void, Boolean>
{
    private menu_activity menuActivity;

    private Integer position;

    public download_internet_check(Context context, Integer inPosition)
    {
        menuActivity = (menu_activity)context;

        position = inPosition;
    }

    public Boolean doInBackground(Void... args)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) menuActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

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

    protected void onPostExecute(Boolean argument)
    {
        menuActivity.toast[menuActivity.toastInteger].cancel();

        if(menuActivity.toastInteger == 0)
        {
            menuActivity.toastInteger++;
        }
        else
        {
            menuActivity.toastInteger--;
        }

        Toast toast = menuActivity.toast[menuActivity.toastInteger];
        toast.setDuration(Toast.LENGTH_SHORT);

        if(!argument)
        {
            toast.setText(R.string.internet);
        }
        else
        {
            if (!menuActivity.puzzleListBoolean)
            {
                menuActivity.puzzleListBoolean = true;

                menuActivity.GoToDownloadTasks(new String[]{"GetPuzzle", String.valueOf(position)});

                toast.setText(R.string.download_puzzle);
            }
            else
            {
                toast.setText(R.string.downloading_puzzle);
            }
        }

        toast.show();
    }
}