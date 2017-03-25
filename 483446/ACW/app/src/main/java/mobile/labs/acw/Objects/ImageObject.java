package mobile.labs.acw.Objects;

import android.graphics.Bitmap;

public class ImageObject
{
    private Bitmap bitmap;
    private Boolean bitmapBoolean;

    public  ImageObject()
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
