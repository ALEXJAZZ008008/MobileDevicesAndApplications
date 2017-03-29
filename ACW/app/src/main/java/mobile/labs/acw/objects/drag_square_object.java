package mobile.labs.acw.objects;

public class drag_square_object
{
    private two_dimensional_vector_object square, position;

    public drag_square_object(two_dimensional_vector_object inSquare, two_dimensional_vector_object inPosition)
    {
        square = inSquare;
        position = inPosition;
    }

    public void setSquare(two_dimensional_vector_object newSquare)
    {
        square = newSquare;
    }

    public two_dimensional_vector_object getSquare()
    {
        return square;
    }

    public void setPosition(two_dimensional_vector_object newPosition)
    {
        position = newPosition;
    }

    public two_dimensional_vector_object getPosition()
    {
        return position;
    }
}