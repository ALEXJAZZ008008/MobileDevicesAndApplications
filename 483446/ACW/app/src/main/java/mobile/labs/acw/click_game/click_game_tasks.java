package mobile.labs.acw.click_game;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import java.net.URL;
import java.util.ArrayList;
import mobile.labs.acw.objects.image_object;
import mobile.labs.acw.objects.puzzle_object;

//Please see menu_tasks for comments code is similar with minor changes
public class click_game_tasks extends AsyncTask<puzzle_object, Void, puzzle_object>
{
    private click_game_activity clickGameActivity;

    private ArrayList<ArrayList<String>> jsonArrays;
    private ArrayList<image_object> imageArray;

    private Integer length;

    private String parentURL, imagesURL, pictureSetsURL, jsonExtension;

    public click_game_tasks(Context context, ArrayList<image_object> inImageArray, Integer inLength)
    {
        clickGameActivity = (click_game_activity) context;

        jsonArrays = new ArrayList<>();
        imageArray = inImageArray;

        length = inLength;

        parentURL = "http://www.hull.ac.uk/php/349628/08027/acw/";
        imagesURL = "images/";
        pictureSetsURL = "picturesets/";
        jsonExtension = ".json";
    }

    protected puzzle_object doInBackground(puzzle_object... args)
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

    private void GetPuzzleImages(puzzle_object puzzleObject)
    {
        String pictureSet = puzzleObject.GetPictureSet();

        GoToJSON("null", new String[] { "PictureFiles" }, parentURL + pictureSetsURL + pictureSet + jsonExtension);

        WaitForJSON();

        ArrayList<String> currentArray = jsonArrays.get(0);
        length = currentArray.size();

        for(Integer i = 0; i < length; i++)
        {
            String itemName = currentArray.get(i);

            imageArray.add(new image_object());

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
            new click_game_json(clickGameActivity, jsonArrays, key, arguments).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
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
            new click_game_images(clickGameActivity, imageArray.get(i), fileName, itemName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URL(url));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void WaitForImages(Integer length, ArrayList<image_object> imageArray)
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

    protected void onPostExecute(puzzle_object puzzleObject)
    {
        if(puzzleObject != null)
        {
            clickGameActivity.length = length;
        }
        else
        {
            if(length != 0)
            {
                clickGameActivity.clickGame = new click_game(clickGameActivity, clickGameActivity.puzzle, clickGameActivity.imageArray, clickGameActivity.relativeLayout);
                clickGameActivity.relativeLayout.addView(clickGameActivity.clickGame);

                clickGameActivity.relativeLayout.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        return clickGameActivity.clickGame.onTouch(motionEvent);
                    }
                });
            }
            else
            {
                clickGameActivity.GoToTasks(new puzzle_object[] { });
            }
        }
    }
}