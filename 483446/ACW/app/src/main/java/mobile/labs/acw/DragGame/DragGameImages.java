package mobile.labs.acw.DragGame;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import mobile.labs.acw.Objects.ImageObject;

public class DragGameImages extends AsyncTask<URL, Void, Bitmap>
{
    private DragGameActivity dragGameActivity;

    private ImageObject imageObject;
    private String fileName, itemName;

    public DragGameImages(Context context, ImageObject inImage, String inFileName, String inItemName)
    {
        dragGameActivity = (DragGameActivity)context;

        imageObject = inImage;

        fileName = inFileName;
        itemName = inItemName;
    }

    protected Bitmap doInBackground(URL... url)
    {
        Bitmap bitmap = null;

        ContextWrapper contextWrapper = new ContextWrapper(dragGameActivity);
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
