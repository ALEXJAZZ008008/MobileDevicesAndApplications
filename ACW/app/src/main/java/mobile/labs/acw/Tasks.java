package mobile.labs.acw;

import android.content.Context;
import android.os.AsyncTask;
import java.net.URL;
import java.util.ArrayList;

public class Tasks extends AsyncTask<String, Void, String>
{
    private MenuActivity menuActivity;

    private ArrayList<ArrayList<String>> jsonArrays;

    private String parentURL, imagesURL, indexURL, pictureSetsURL, puzzlesURL, jsonExtension;

    public Tasks(Context context)
    {
        menuActivity = (MenuActivity)context;

        jsonArrays = new ArrayList<>();

        parentURL = "http://www.hull.ac.uk/php/349628/08027/acw/";
        imagesURL = "images/";
        jsonExtension = ".json";
        indexURL = "index" + jsonExtension;
        pictureSetsURL = "picturesets/";
        puzzlesURL = "puzzles/";
    }

    protected String doInBackground(String... args)
    {
        switch(args[0])
        {
            case "StartList":

                StartList();
                break;
        }

        return args[0];
    }

    private void StartList()
    {
        GoToJSON("PuzzleIndex", new String[] { }, parentURL + indexURL);

        while (jsonArrays.size() == 0)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < jsonArrays.get(0).size(); i++)
        {
            menuActivity.puzzleList.add(new Puzzle(menuActivity, jsonArrays.get(0).get(i).split(jsonExtension)[0]));
        }
    }

    private void GoToJSON(String key, String[] arguments, String url)
    {
        try
        {
            new JSON(jsonArrays, key, arguments).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void onPostExecute(String arg)
    {
        switch(arg)
        {
            case "StartList":

                menuActivity.customAdapter.notifyDataSetChanged();
                break;
        }
    }
}