package mobile.labs.acw;

import android.graphics.Bitmap;

public class Square
{
    private Bitmap image;
    private int imagePosition;
    private TwoDimensionalVector position, size;

    public Square(Bitmap inImage, int inImagePosition, TwoDimensionalVector inPosition, TwoDimensionalVector inSize)
    {
        SetImage(inImage);
        SetImagePosition(inImagePosition);
        SetPosition(inPosition);
        SetSize(inSize);
    }

    public void SetImage(Bitmap newImage)
    {
        image = newImage;
    }

    public Bitmap GetImage()
    {
        return image;
    }

    public void SetImagePosition(int newImagePosition)
    {
        imagePosition = newImagePosition;
    }

    public int GetImagePosition()
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

    public void SetSize(TwoDimensionalVector newSize)
    {
        size = newSize;
    }

    public TwoDimensionalVector GetSize()
    {
        return size;
    }
}
