package mobile.labs.acw;

public class TwoDimensionalVectorObject
{
    private Integer x, y;

    public TwoDimensionalVectorObject(Integer inX, Integer inY)
    {
        SetX(inX);
        SetY(inY);
    }

    public void SetX(Integer newX)
    {
        x = newX;
    }

    public Integer GetX()
    {
        return x;
    }

    public void SetY(Integer newY)
    {
        y = newY;
    }

    public Integer GetY()
    {
        return y;
    }
}
