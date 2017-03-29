package mobile.labs.acw.objects;

public class two_dimensional_vector_object
{
    private Integer x, y;

    public two_dimensional_vector_object(Integer inX, Integer inY)
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
