package mobile.labs.acw.menu;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import mobile.labs.acw.R;
import mobile.labs.acw.objects.image_object;

public class menu_images extends AsyncTask<URL, Void, Bitmap>
{
    private menu_activity menuActivity;

    private image_object imageObject;
    private String fileName, itemName;

    public menu_images(Context context, image_object inImage, String inFileName, String inItemName)
    {
        menuActivity = (menu_activity)context;

        imageObject = inImage;

        fileName = inFileName;
        itemName = inItemName;
    }

    protected Bitmap doInBackground(URL... url)
    {
        Bitmap bitmap = null;

        ContextWrapper contextWrapper = new ContextWrapper(menuActivity);
        File file = contextWrapper.getDir(fileName, Context.MODE_PRIVATE);
        File item = new File(file, itemName);

        try
        {
            FileInputStream reader = new FileInputStream(item);
            bitmap = BitmapFactory.decodeStream(reader);
        }
        catch(Exception e)
        {
            try
            {
                bitmap = BitmapFactory.decodeStream((InputStream)url[0].getContent());

                FileOutputStream writer = null;

                try
                {
                    writer = new FileOutputStream(item);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, writer);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    if(writer != null)
                    {
                        writer.close();
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap)
    {
        if(bitmap != null)
        {
            imageObject.SetBitmap(bitmap);
            imageObject.SetBitmapBoolean(true);
        }
        else
        {
            Toast.makeText(menuActivity, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}
