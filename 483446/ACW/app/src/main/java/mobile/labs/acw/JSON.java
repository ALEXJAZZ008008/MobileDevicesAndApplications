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
        ArrayList<ArrayList<String>> resultLists = new ArrayList<>();

        try
        {
            String result = Input(url[0]);

            if(key.equals("null"))
            {
                resultLists.add(SingleArray(result, arguments[0]));
            }
            else
            {
                ArrayList<ArrayList<String>> returnLists = MultipleObjects(result);

                for(ArrayList<String> arrayList : returnLists)
                {
                    resultLists.add(arrayList);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return resultLists;
    }

    private String Input(URL url)
    {
        String result = "";

        try
        {
            InputStream inputStream = (InputStream) url.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            while (line != null)
            {
                result += line;
                line = bufferedReader.readLine();

                if (isCancelled())
                {
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<ArrayList<String>> MultipleObjects(String result)
    {
        ArrayList<ArrayList<String>> resultLists = new ArrayList<>();

        try
        {
            JSONObject jsonObject = new JSONObject(result).getJSONObject(key);

            for (String string : arguments)
            {
                Object item = jsonObject.get(string);

                if (item instanceof JSONArray)
                {
                    resultLists.add(ArrayObject((JSONArray)item));
                }
                else
                {
                    resultLists.add(SingleObject(item));
                }

                if (isCancelled())
                {
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return resultLists;
    }

    private ArrayList<String> SingleArray(String result, String argument)
    {
        ArrayList<String> resultList = new ArrayList<>();

        try
        {
            JSONArray jsonArray = new JSONObject(result).getJSONArray(argument);

            resultList = ArrayObject(jsonArray);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return resultList;
    }

    private ArrayList<String> ArrayObject(JSONArray jsonArray)
    {
        ArrayList<String> resultList = new ArrayList<>();

        try
        {
            for (int j = 0; j < jsonArray.length(); j++)
            {
                resultList.add(jsonArray.get(j).toString());

                if (isCancelled())
                {
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return resultList;
    }

    private ArrayList<String> SingleObject(Object item)
    {
        ArrayList<String> resultList = new ArrayList<>();

        resultList.add(item.toString());

        return resultList;
    }

    protected void onPostExecute(ArrayList<ArrayList<String>> resultLists)
    {
        for(int i = 0; i < resultLists.size(); i++)
        {
            jsonArrays.add(resultLists.get(i));
        }
    }
}