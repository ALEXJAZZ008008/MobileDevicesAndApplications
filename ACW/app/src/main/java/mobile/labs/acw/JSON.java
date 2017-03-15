package mobile.labs.acw;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class JSON extends AsyncTask<URL, Void, ArrayList<ArrayList<String>>>
{
    private ArrayList<ArrayList<String>> jsonArrays;

    private String key;
    private String[] arguments;

    public JSON(ArrayList<ArrayList<String>> inJSONArrays, String inKey, String[] inArguments)
    {
        jsonArrays = inJSONArrays;

        key = inKey;
        arguments = inArguments;
    }

    protected ArrayList<ArrayList<String>> doInBackground(URL... url)
    {
        String result = "";

        JSONArray jsonArray;

        ArrayList<String> resultList;
        ArrayList<ArrayList<String>> resultLists = new ArrayList<>();

        try
        {
            InputStream inputStream = (InputStream)url[0].getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            while(line != null)
            {
                result += line;
                line = bufferedReader.readLine();

                if (isCancelled())
                {
                    break;
                }
            }

            if(arguments.length == 0)
            {
                resultList = new ArrayList<>();

                jsonArray = new JSONObject(result).getJSONArray(key);

                for (int j = 0; j < jsonArray.length(); j++)
                {
                    resultList.add(jsonArray.get(j).toString());

                    if (isCancelled())
                    {
                        break;
                    }
                }

                resultLists.add(resultList);
            }
            else
            {
                JSONObject jsonObject = new JSONObject(result).getJSONObject(key);

                for(String string : arguments)
                {
                    resultList = new ArrayList<>();

                    Object item = jsonObject.get(string);

                    if (item instanceof JSONArray)
                    {
                        jsonArray = (JSONArray)item;

                        for (int j = 0; j < jsonArray.length(); j++)
                        {
                            resultList.add(jsonArray.get(j).toString());

                            if (isCancelled())
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        resultList.add(item.toString());
                    }

                    jsonArrays.add(resultList);

                    if (isCancelled())
                    {
                        break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return resultLists;
    }

    protected void onPostExecute(ArrayList<ArrayList<String>> resultLists)
    {
        for(int i = 0; i < resultLists.size(); i++)
        {
            jsonArrays.add(resultLists.get(i));
        }
    }
}