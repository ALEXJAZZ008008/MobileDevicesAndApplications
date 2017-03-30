package mobile.labs.acw.click_game;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import mobile.labs.acw.objects.image_object;

//Please see menu_images for comments code is similar with minor changes
public class click_game_images extends AsyncTask<URL, Void, Bitmap>
{
    private click_game_activity clickGameActivity;

    private image_object imageObject;
    private String fileName, itemName;

    public click_game_images(Context context, image_object inImage, String inFileName, String inItemName)
    {
        clickGameActivity = (click_game_activity) context;

        imageObject = inImage;

        fileName = inFileName;
        itemName = inItemName;
    }

    protected Bitmap doInBackground(URL... url)
    {
        Bitmap bitmap = null;

        ContextWrapper contextWrapper = new ContextWrapper(clickGameActivity);
        File file = contextWrapper.getDir(fileName, Context.MODE_PRIVATE);
        File item = new File(file, itemName);

        try
        {
            FileInputStream reader = new FileInputStream(item);
            bitmap = BitmapFactory.decodeStream(reader);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap)
    {
        imageObject.SetBitmap(bitmap);
        imageObject.SetBitmapBoolean(true);
    }
}
