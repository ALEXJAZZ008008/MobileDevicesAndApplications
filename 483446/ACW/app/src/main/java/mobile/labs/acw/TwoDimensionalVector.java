package mobile.labs.acw;

public class TwoDimensionalVector
{
    private float x, y;

    public TwoDimensionalVector(float inX, float inY)
    {
        SetX(inX);
        SetY(inY);
    }

    public void SetX(float newX)
    {
        x = newX;
    }

    public float GetX()
    {
        return x;
    }

    public void SetY(float newY)
    {
        y = newY;
    }

    public float GetY()
    {
        return y;
    }
}
