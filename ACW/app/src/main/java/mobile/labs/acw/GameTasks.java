package mobile.labs.acw;

import android.content.Context;
import android.os.AsyncTask;
import java.net.URL;
import java.util.ArrayList;

public class GameTasks extends AsyncTask<PuzzleObject, Void, PuzzleObject>
{
    private GameActivity gameActivity;

    private ArrayList<ArrayList<String>> jsonArrays;
    private ArrayList<ImageObject> imageArray;

    private String parentURL, imagesURL, pictureSetsURL, jsonExtension;

    public GameTasks(Context context, ArrayList<ImageObject> inImageArray)
    {
        gameActivity = (GameActivity) context;

        jsonArrays = new ArrayList<>();
        imageArray = inImageArray;

        parentURL = "http://www.hull.ac.uk/php/349628/08027/acw/";
        imagesURL = "images/";
        pictureSetsURL = "picturesets/";
        jsonExtension = ".json";
    }

    protected PuzzleObject doInBackground(PuzzleObject... args)
    {
        if(args.length > 0)
        {
            GetPuzzleImages(args[0]);

            return args[0];
        }
        else
        {
            StartCanvas();

            return null;
        }
    }

    private void GetPuzzleImages(PuzzleObject puzzleObject)
    {
        String pictureSet = puzzleObject.GetPictureSet();

        GoToJSON("null", new String[] { "PictureFiles" }, parentURL + pictureSetsURL + pictureSet + jsonExtension);

        WaitForJSON();

        int length = jsonArrays.get(0).size();

        for(int i = 0; i < length; i++)
        {
            String itemName = jsonArrays.get(0).get(i);

            imageArray.add(new ImageObject());

            GoToImage(i, pictureSet, itemName, parentURL + imagesURL + itemName);
        }

        WaitForImages(length);

        jsonArrays.clear();
    }

    private void StartCanvas()
    {
        WaitForImagesBoolean();
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

    private void GoToImage(int i, String fileName, String itemName, String url)
    {
        try
        {
            new GameImages(gameActivity, imageArray.get(i), fileName, itemName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void WaitForImages(int length)
    {
        for(int i = 0; i < length; i++)
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

    private void WaitForImagesBoolean()
    {
        while (!gameActivity.imageArrayBoolean)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void onPostExecute(PuzzleObject puzzleObject)
    {
        if(puzzleObject != null)
        {
            gameActivity.imageArrayBoolean = true;
        }
        else
        {
            gameActivity.canvas = new Canvas(gameActivity);
            gameActivity.relativeLayout.addView(gameActivity.canvas);
        }
    }
}