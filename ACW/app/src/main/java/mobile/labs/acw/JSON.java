package mobile.labs.acw;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class JSON extends AsyncTask<String, String, ArrayList<String>>
{
    private MenuActivity menuActivity;
    private String switchString;

    public JSON(Context context, String arg)
    {
        menuActivity = (MenuActivity)context;

        switchString = arg;
    }

    protected ArrayList<String> doInBackground(String... args)
    {
        String result = "";
        ArrayList<String> resultList = new ArrayList<>();

        try
        {
            InputStream inputStream = (InputStream)new URL(args[0]).getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            while(line != null)
            {
                result += line;
                line = bufferedReader.readLine();
            }

            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = jsonObject.getJSONArray(switchString);

            result = "";

            for(int i = 0; i < jsonArray.length(); i++)
            {
                resultList.add(jsonArray.get(i).toString());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return resultList;
    }

    protected void onPostExecute(ArrayList<String> resultList)
    {
        menuActivity.jsonArray = resultList;
    }
}