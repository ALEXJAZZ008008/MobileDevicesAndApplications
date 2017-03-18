package mobile.labs.acw;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Canvas extends SurfaceView implements SurfaceHolder.Callback
{
    private PuzzleObject puzzle;
    private Bitmap[] imageArray;

    private TwoDimensionalVector canvasSize;

    private Square[][] squares;

    public Canvas(Context context)
    {
        super(context);
    }

    public Canvas(Context context, PuzzleObject inPuzzle, ArrayList<ImageObject> inImageArray, RelativeLayout inRelativeLayout)
    {
        super(context);

        puzzle = inPuzzle;

        imageArray = GetBitmapImageArray(inImageArray);

        canvasSize = new TwoDimensionalVector(inRelativeLayout.getWidth(), inRelativeLayout.getHeight());

        Initialise();
    }

    private Bitmap[] GetBitmapImageArray(ArrayList<ImageObject> inImageArray)
    {
        ImageObject[] imageArrayImageObjectArray = GetImageArrayImageObjectArray(inImageArray);

        return GetImageArrayBitmapArray(imageArrayImageObjectArray);
    }

    private ImageObject[] GetImageArrayImageObjectArray(ArrayList<ImageObject> inImageArray)
    {
        ImageObject[] imageArrayImageObjectArray = new ImageObject[inImageArray.size()];

        for(int i = 0; i < imageArrayImageObjectArray.length; i++)
        {
            imageArrayImageObjectArray[i] = inImageArray.get(i);
        }

        return imageArrayImageObjectArray;
    }

    private Bitmap[] GetImageArrayBitmapArray(ImageObject[] imageArrayImageObjectArray)
    {
        Bitmap[] imageArrayBitmapArray = new Bitmap[imageArrayImageObjectArray.length];

        for(int i = 0; i < imageArrayBitmapArray.length; i++)
        {
            imageArrayBitmapArray[i] = imageArrayImageObjectArray[i].GetBitmap();
        }

        return imageArrayBitmapArray;
    }

    private void Initialise()
    {
        int rows = Integer.valueOf(puzzle.GetRows());
        int columns = puzzle.GetLayout().size() / rows;

        TwoDimensionalVector size = new TwoDimensionalVector((canvasSize.GetX() / rows), (canvasSize.GetY() / columns));

        squares = new Square[rows][columns];

        GetSquares(rows, columns, size);
    }

    private int[] GetLayout()
    {
        ArrayList<String> layout = puzzle.GetLayout();
        int length = layout.size();

        String[] layoutStringArray = new String[length];

        for(int i = 0; i < length; i++)
        {
            layoutStringArray[i] = layout.get(i);
        }

        int[] layoutIntArray = new int[length];

        for(int i = 0; i < length; i++)
        {
            layoutIntArray[i] = Integer.valueOf(layoutStringArray[i]);
        }

        return layoutIntArray;
    }

    private void GetSquares(int rows, int columns, TwoDimensionalVector size)
    {
        int[] layout = GetLayout();

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                int layoutInt = layout[(i * columns) + j];

                TwoDimensionalVector position = new TwoDimensionalVector((size.GetX() * i), (size.GetY() * j));

                squares[i][j] = new Square(imageArray[layoutInt], layoutInt, position, size);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolderolder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolderolder)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolderolder)
    {

    }
}
