package mobile.labs.acw.objects;

import android.graphics.Bitmap;

public class square_object
{
    private Bitmap image;
    private Integer imageState, imagePosition;
    private two_dimensional_vector_object position;

    public square_object(Bitmap inImage, Integer inImageState, Integer inImagePosition, two_dimensional_vector_object inPosition)
    {
        SetImage(inImage);
        SetImageState(inImageState);
        SetImagePosition(inImagePosition);
        SetPosition(inPosition);
    }

    public void SetImage(Bitmap newImage)
    {
        image = newImage;
    }

    public Bitmap GetImage()
    {
        return image;
    }

    public void SetImageState(Integer newImageState)
    {
        imageState = newImageState;
    }

    public Integer GetImageState()
    {
        return imageState;
    }

    public void SetImagePosition(Integer newImagePosition)
    {
        imagePosition = newImagePosition;
    }

    public Integer GetImagePosition()
    {
        return imagePosition;
    }

    public void SetPosition(two_dimensional_vector_object newPosition)
    {
        position = newPosition;
    }

    public two_dimensional_vector_object GetPosition()
    {
        return position;
    }
}
