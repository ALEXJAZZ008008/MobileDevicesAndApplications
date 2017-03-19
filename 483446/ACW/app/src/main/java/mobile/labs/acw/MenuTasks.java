package mobile.labs.acw;

import android.content.Context;
import android.os.AsyncTask;
import java.net.URL;
import java.util.ArrayList;

public class MenuTasks extends AsyncTask<String, Void, String>
{
    private MenuActivity menuActivity;

    private ArrayList<PuzzleListItemObject> puzzleList;

    private ArrayList<ArrayList<String>> jsonArrays;
    private ArrayList<ImageObject> imageArray;

    private String parentURL, imagesURL, indexURL, pictureSetsURL, puzzlesURL, jsonExtension;

    public MenuTasks(Context context, ArrayList<PuzzleListItemObject> inPuzzleList)
    {
        menuActivity = (MenuActivity)context;

        puzzleList = inPuzzleList;

        jsonArrays = new ArrayList<>();
        imageArray = new ArrayList<>();

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

            case "GetPuzzle":

                GetPuzzle(args[1]);
                break;

            case "GetPuzzleImages":

                GetPuzzleImages(args[1]);
                break;
        }

        return args[0];
    }

    private void StartList()
    {
        GoToJSON("null", new String[] { "PuzzleIndex" }, parentURL + indexURL);

        WaitForJSON();

        ArrayList<String> currentArray = jsonArrays.get(0);

        for(Integer i = 0; i < currentArray.size(); i++)
        {
            puzzleList.add(new PuzzleListItemObject(menuActivity, currentArray.get(i).split(jsonExtension)[0]));
        }

        jsonArrays.clear();
    }

    private void GetPuzzle(String arg)
    {
        PuzzleListItemObject puzzleListItem = puzzleList.get(Integer.valueOf(arg));

        GoToJSON("Puzzle", new String[] { "Id", "PictureSet", "Rows", "Layout" }, parentURL + puzzlesURL + puzzleListItem.GetTitle() + jsonExtension);

        WaitForJSON();

        PuzzleObject puzzleObject = puzzleList.get(Integer.valueOf(arg)).GetPuzzle();

        ArrayList<String> layoutList = new ArrayList<>();

        puzzleObject.SetPictureSet(jsonArrays.get(1).get(0).split(jsonExtension)[0]);

        GoToTasks(new String[] { "GetPuzzleImages", arg });

        puzzleObject.SetId(jsonArrays.get(0).get(0));
        puzzleObject.SetRows(jsonArrays.get(2).get(0));

        ArrayList<String> currentArray = jsonArrays.get(3);

        for(Integer i = 0; i < currentArray.size(); i++)
        {
            layoutList.add(currentArray.get(i));
        }

        jsonArrays.clear();

        puzzleObject.SetLayout(layoutList);
    }

    private void GetPuzzleImages(String arg)
    {
        PuzzleListItemObject puzzleListItem = puzzleList.get(Integer.valueOf(arg));
        PuzzleObject puzzleObject = puzzleListItem.GetPuzzle();
        String pictureSet = puzzleObject.GetPictureSet();

        GoToJSON("null", new String[] { "PictureFiles" }, parentURL + pictureSetsURL + pictureSet + jsonExtension);

        WaitForJSON();

        ArrayList<String> currentArray = jsonArrays.get(0);
        Integer length = currentArray.size();

        for(Integer i = 0; i < length; i++)
        {
            String itemName = currentArray.get(i);

            imageArray.add(new ImageObject());

            GoToImages(i, pictureSet, itemName, parentURL + imagesURL + itemName);
        }

        WaitForImages(length);

        jsonArrays.clear();
        imageArray.clear();

        puzzleListItem.SetState(menuActivity.getResources().getString(R.string.play));
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

    private void WaitForJSON()
    {
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
    }

    private void GoToTasks(String[] taskArgs)
    {
        new MenuTasks(menuActivity, puzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    private void GoToImages(Integer i, String fileName, String itemName, String url)
    {
        try
        {
            new MenuImages(menuActivity, imageArray.get(i), fileName, itemName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void WaitForImages(Integer length)
    {
        for(Integer i = 0; i < length; i++)
        {
            while (!imageArray.get(i).GetBitmapBoolean())
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onPostExecute(String arg)
    {
        switch(arg)
        {
            case "StartList":

                menuActivity.customAdapter.notifyDataSetChanged();
                break;

            case "GetPuzzleImages":

                menuActivity.puzzleListBoolean = false;

                menuActivity.customAdapter.notifyDataSetChanged();
                break;

            default:

                break;
        }
    }
}