package mobile.labs.acw.DragGame;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class DragGameJSON extends AsyncTask<URL, Void, ArrayList<ArrayList<String>>>
{
    private Activity dragGameActivity;

    private ArrayList<ArrayList<String>> jsonArrays;

    private String key;
    private String[] arguments;

    public DragGameJSON(Context context, ArrayList<ArrayList<String>> inJSONArrays, String inKey, String[] inArguments)
    {
        dragGameActivity = (Activity)context;

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

        ContextWrapper contextWrapper = new ContextWrapper(dragGameActivity);
        File file = contextWrapper.getDir("JSON", Context.MODE_PRIVATE);

        String itemName = url.getFile().replace('/','_') + "_" + key;

        for(String string : arguments)
        {
            itemName += "_" + string;

            if (isCancelled())
            {
                break;
            }
        }

        File item = new File(file, itemName);
        FileInputStream fileInputStream = null;
        FileWriter fileWriter = null;

        try
        {
            fileInputStream = new FileInputStream(item);

            Integer size = fileInputStream.available();
            byte[] buffer = new byte[size];

            Integer bytesRead = fileInputStream.read(buffer);
            fileInputStream.close();

            result = new String(buffer);
        }
        catch(Exception e)
        {
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

                fileWriter = new FileWriter(item);
                fileWriter.write(result);
                fileWriter.flush();
                fileWriter.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    if (fileWriter != null)
                    {
                        fileWriter.close();
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        finally
        {
            try
            {
                if (fileInputStream != null)
                {
                    fileInputStream.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
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
            for (Integer i = 0; i < jsonArray.length(); i++)
            {
                resultList.add(jsonArray.get(i).toString());

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
        for(Integer i = 0; i < resultLists.size(); i++)
        {
            jsonArrays.add(resultLists.get(i));
        }
    }
}