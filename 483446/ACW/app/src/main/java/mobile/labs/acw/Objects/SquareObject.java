package mobile.labs.acw.Objects;

import android.graphics.Bitmap;

public class SquareObject
{
    private Bitmap image;
    private Integer imageState, imagePosition;
    private TwoDimensionalVectorObject position;

    public SquareObject(Bitmap inImage, Integer inImageState, Integer inImagePosition, TwoDimensionalVectorObject inPosition)
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

    public void SetPosition(TwoDimensionalVectorObject newPosition)
    {
        position = newPosition;
    }

    public TwoDimensionalVectorObject GetPosition()
    {
        return position;
    }
}
