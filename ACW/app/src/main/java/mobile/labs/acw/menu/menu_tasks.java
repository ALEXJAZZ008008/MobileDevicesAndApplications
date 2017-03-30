package mobile.labs.acw.menu;

import android.content.Context;
import android.os.AsyncTask;
import java.net.URL;
import java.util.ArrayList;
import mobile.labs.acw.objects.image_object;
import mobile.labs.acw.utilities.preferences;
import mobile.labs.acw.objects.puzzle_list_item_object;
import mobile.labs.acw.objects.puzzle_object;
import mobile.labs.acw.R;

public class menu_tasks extends AsyncTask<String, Void, String>
{
    private menu_activity menuActivity;

    private ArrayList<puzzle_list_item_object> puzzleList;

    private ArrayList<ArrayList<String>> jsonArrays;
    private ArrayList<image_object> imageArray;

    private String parentURL, imagesURL, indexURL, pictureSetsURL, puzzlesURL, jsonExtension;

    public menu_tasks(Context context, ArrayList<puzzle_list_item_object> inPuzzleList)
    {
        menuActivity = (menu_activity)context;

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
        //This selects the correct task
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

    //This starts the puzzle list
    private void StartList()
    {
        GoToJSON("null", new String[] { "PuzzleIndex" }, parentURL + indexURL);

        WaitForJSON();

        ArrayList<String> currentArray = jsonArrays.get(0);

        for(Integer i = 0; i < currentArray.size(); i++)
        {
            puzzleList.add(new puzzle_list_item_object(menuActivity, currentArray.get(i).split(jsonExtension)[0]));
        }

        jsonArrays.clear();
    }

    //This downloads a given puzzle
    private void GetPuzzle(String arg)
    {
        puzzle_list_item_object puzzleListItem = puzzleList.get(Integer.valueOf(arg));

        GoToJSON("Puzzle", new String[] { "Id", "PictureSet", "Rows", "Layout" }, parentURL + puzzlesURL + puzzleListItem.GetTitle() + jsonExtension);

        WaitForJSON();

        puzzle_object puzzleObject = puzzleList.get(Integer.valueOf(arg)).GetPuzzle();

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

        String Id = puzzleObject.GetId();

        preferences.WriteString(menuActivity, puzzleListItem.GetTitle(), Id);

        preferences.WriteString(menuActivity, Id + "PictureSet", puzzleObject.GetPictureSet());
        preferences.WriteString(menuActivity, Id + "Rows", puzzleObject.GetRows());

        ArrayList<String> puzzleListItemPuzzleLayout = puzzleObject.GetLayout();

        for(Integer j = 0; j < puzzleListItemPuzzleLayout.size(); j++)
        {
            preferences.WriteString(menuActivity, Id + "Layout" + String.valueOf(j), puzzleListItemPuzzleLayout.get(j));
        }
    }

    //This downloads all the images for a given puzzle
    private void GetPuzzleImages(String arg)
    {
        puzzle_list_item_object puzzleListItem = puzzleList.get(Integer.valueOf(arg));
        puzzle_object puzzleObject = puzzleListItem.GetPuzzle();
        String pictureSet = puzzleObject.GetPictureSet();

        GoToJSON("null", new String[] { "PictureFiles" }, parentURL + pictureSetsURL + pictureSet + jsonExtension);

        WaitForJSON();

        ArrayList<String> currentArray = jsonArrays.get(0);
        Integer length = currentArray.size();

        for(Integer i = 0; i < length; i++)
        {
            String itemName = currentArray.get(i);

            imageArray.add(new image_object());

            GoToImages(i, pictureSet, itemName, parentURL + imagesURL + itemName);
        }

        WaitForImages(length);

        jsonArrays.clear();
        imageArray.clear();

        puzzleListItem.SetState(menuActivity.getResources().getString(R.string.play));

        String puzzleListItemPuzzleId = puzzleListItem.GetPuzzle().GetId();

        preferences.WriteString(menuActivity, puzzleListItemPuzzleId + "state", puzzleListItem.GetState());
    }

    //This gets the json for a given input
    private void GoToJSON(String key, String[] arguments, String url)
    {
        try
        {
            new menu_json(menuActivity, jsonArrays, key, arguments).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //This ensures all relevant json is downloaded before continuing
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
        new menu_tasks(menuActivity, puzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    //This starts the image task
    private void GoToImages(Integer i, String fileName, String itemName, String url)
    {
        try
        {
            new menu_images(menuActivity, imageArray.get(i), fileName, itemName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //This waits for all images to be downloaded
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

                menuActivity.listStartedBoolean = true;

                menuActivity.SetAndUpdateLists();
                break;

            case "GetPuzzleImages":

                menuActivity.puzzleListBoolean = false;

                menuActivity.SetAndUpdateLists();
                break;

            default:

                break;
        }
    }
}