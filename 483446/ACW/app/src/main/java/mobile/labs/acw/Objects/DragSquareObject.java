package mobile.labs.acw.Objects;

public class DragSquareObject
{
    private TwoDimensionalVectorObject square, position;

    public DragSquareObject(TwoDimensionalVectorObject inSquare, TwoDimensionalVectorObject inPosition)
    {
        square = inSquare;
        position = inPosition;
    }

    public void setSquare(TwoDimensionalVectorObject newSquare)
    {
        square = newSquare;
    }

    public TwoDimensionalVectorObject getSquare()
    {
        return square;
    }

    public void setPosition(TwoDimensionalVectorObject newPosition)
    {
        position = newPosition;
    }

    public TwoDimensionalVectorObject getPosition()
    {
        return position;
    }
}