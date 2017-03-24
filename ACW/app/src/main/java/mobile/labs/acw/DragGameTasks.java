package mobile.labs.acw;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

import java.net.URL;
import java.util.ArrayList;

public class DragGameTasks extends AsyncTask<PuzzleObject, Void, PuzzleObject>
{
    private DragGameActivity dragGameActivity;

    private ArrayList<ArrayList<String>> jsonArrays;
    private ArrayList<ImageObject> imageArray;

    private Integer length;

    private String parentURL, imagesURL, pictureSetsURL, jsonExtension;

    public DragGameTasks(Context context, ArrayList<ImageObject> inImageArray, Integer inLength)
    {
        dragGameActivity = (DragGameActivity) context;

        jsonArrays = new ArrayList<>();
        imageArray = inImageArray;

        length = inLength;

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
            CheckPuzzleImages();

            return null;
        }
    }

    private void GetPuzzleImages(PuzzleObject puzzleObject)
    {
        String pictureSet = puzzleObject.GetPictureSet();

        GoToJSON("null", new String[] { "PictureFiles" }, parentURL + pictureSetsURL + pictureSet + jsonExtension);

        WaitForJSON();

        ArrayList<String> currentArray = jsonArrays.get(0);
        length = currentArray.size();

        for(Integer i = 0; i < length; i++)
        {
            String itemName = currentArray.get(i);

            imageArray.add(new ImageObject());

            GoToImage(i, pictureSet, itemName, parentURL + imagesURL + itemName);
        }

        WaitForImages(length, imageArray);

        jsonArrays.clear();
    }

    private void CheckPuzzleImages()
    {
        if(length != 0)
        {
            WaitForImages(length, imageArray);
        }
    }

    private void GoToJSON(String key, String[] arguments, String url)
    {
        try
        {
            new DragGameJSON(dragGameActivity, jsonArrays, key, arguments).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
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

    private void GoToImage(Integer i, String fileName, String itemName, String url)
    {
        try
        {
            new DragGameImages(dragGameActivity, imageArray.get(i), fileName, itemName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void WaitForImages(Integer length, ArrayList<ImageObject> imageArray)
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

    protected void onPostExecute(PuzzleObject puzzleObject)
    {
        if(puzzleObject != null)
        {
            dragGameActivity.length = length;
        }
        else
        {
            if(length != 0)
            {
                dragGameActivity.dragGame = new DragGame(dragGameActivity, dragGameActivity.puzzle, dragGameActivity.imageArray, dragGameActivity.relativeLayout);
                dragGameActivity.relativeLayout.addView(dragGameActivity.dragGame);

                dragGameActivity.relativeLayout.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        return dragGameActivity.dragGame.onTouch(motionEvent);
                    }
                });
            }
            else
            {
                dragGameActivity.GoToTasks(new PuzzleObject[] { });
            }
        }
    }
}