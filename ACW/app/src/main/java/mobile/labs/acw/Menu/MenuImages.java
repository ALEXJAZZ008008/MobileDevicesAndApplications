package mobile.labs.acw.Menu;

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
import mobile.labs.acw.Objects.ImageObject;

public class MenuImages extends AsyncTask<URL, Void, Bitmap>
{
    private MenuActivity menuActivity;

    private ImageObject imageObject;
    private String fileName, itemName;

    public MenuImages(Context context, ImageObject inImage, String inFileName, String inItemName)
    {
        menuActivity = (MenuActivity)context;

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
            Toast.makeText(menuActivity, "Image Does Not Exist and Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}
