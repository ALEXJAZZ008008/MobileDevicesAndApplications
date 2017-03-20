package mobile.labs.acw;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class GameImages extends AsyncTask<URL, Void, Bitmap>
{
    private ClickGameActivity clickGameActivity;

    private ImageObject imageObject;
    private String fileName, itemName;

    public GameImages(Context context, ImageObject inImage, String inFileName, String inItemName)
    {
        clickGameActivity = (ClickGameActivity) context;

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
