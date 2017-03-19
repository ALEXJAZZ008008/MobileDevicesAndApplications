package mobile.labs.acw;

import android.graphics.Bitmap;

public class Square
{
    private Bitmap image;
    private Integer imagePosition;
    private TwoDimensionalVector position;

    public Square(Bitmap inImage, Integer inImagePosition, TwoDimensionalVector inPosition)
    {
        SetImage(inImage);
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

    public void SetImagePosition(Integer newImagePosition)
    {
        imagePosition = newImagePosition;
    }

    public Integer GetImagePosition()
    {
        return imagePosition;
    }

    public void SetPosition(TwoDimensionalVector newPosition)
    {
        position = newPosition;
    }

    public TwoDimensionalVector GetPosition()
    {
        return position;
    }
}
