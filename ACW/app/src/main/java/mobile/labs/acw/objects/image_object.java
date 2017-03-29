package mobile.labs.acw.objects;

import android.graphics.Bitmap;

public class image_object
{
    private Bitmap bitmap;
    private Boolean bitmapBoolean;

    public image_object()
    {
        SetBitmapBoolean(false);
    }

    public void SetBitmap(Bitmap newBitmap)
    {
        bitmap = newBitmap;
    }

    public Bitmap GetBitmap()
    {
        return bitmap;
    }

    public void SetBitmapBoolean(Boolean newBitmapBoolean)
    {
        bitmapBoolean = newBitmapBoolean;
    }

    public Boolean GetBitmapBoolean()
    {
        return bitmapBoolean;
    }
}
