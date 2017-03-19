package mobile.labs.acw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Game extends SurfaceView implements SurfaceHolder.Callback
{
    private GameActivity gameActivity;

    private PuzzleObject puzzle;
    private ArrayList<Bitmap> imageArray;

    private TwoDimensionalVector canvasSize;

    private ArrayList<ArrayList<Square>> squares;

    private Paint paint;
    private Thread thread;

    public Game(Context context)
    {
        super(context);
    }

    public Game(Context context, PuzzleObject inPuzzle, ArrayList<ImageObject> inImageArray, RelativeLayout inRelativeLayout)
    {
        super(context);

        gameActivity = (GameActivity)context;

        puzzle = inPuzzle;

        imageArray = GetBitmapImageArray(inImageArray);

        canvasSize = new TwoDimensionalVector(inRelativeLayout.getWidth(), inRelativeLayout.getHeight());

        Initialise();
    }

    private ArrayList<Bitmap> GetBitmapImageArray(ArrayList<ImageObject> inImageArray)
    {
        ArrayList<Bitmap> imageArrayBitmapArray = new ArrayList<>();

        for(Integer i = 0; i < inImageArray.size(); i++)
        {
            imageArrayBitmapArray.add(inImageArray.get(i).GetBitmap());
        }

        return imageArrayBitmapArray;
    }

    private void Initialise()
    {
        Integer rows = Integer.valueOf(puzzle.GetRows());
        Integer columns = puzzle.GetLayout().size() / rows;

        TwoDimensionalVector size = new TwoDimensionalVector((canvasSize.GetX() / columns), (canvasSize.GetY() / rows));

        squares = new ArrayList<>();

        GetSquares(rows, columns, size);

        paint = new Paint();
        gameActivity.surfaceHolder = getHolder();
        gameActivity.surfaceHolder.addCallback(this);
    }

    private void GetSquares(Integer rows, Integer columns, TwoDimensionalVector size)
    {
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            squares.add(new ArrayList<Square>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                TwoDimensionalVector position = new TwoDimensionalVector((size.GetX() * j), (size.GetY() * i));

                squares.get(i).add(new Square(Bitmap.createScaledBitmap(imageArray.get(layoutInteger), size.GetX(), size.GetY(), false), layoutInteger, position));
            }
        }
    }

    private ArrayList<Integer> GetLayout(ArrayList<String> layout)
    {
        ArrayList<Integer> layoutIntegerArray = new ArrayList<>();

        for(Integer i = 0; i < layout.size(); i++)
        {
            layoutIntegerArray.add(Integer.valueOf(layout.get(i)));
        }

        return layoutIntegerArray;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolderolder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolderolder)
    {
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Canvas canvas;

                while (!Thread.currentThread().isInterrupted())
                {
                    canvas = gameActivity.surfaceHolder.lockCanvas();

                    canvas.drawColor(Color.parseColor("#000000"));

                    for(int i = 0; i < squares.size(); i++)
                    {
                        ArrayList<Square> currentRow = squares.get(i);

                        for(int j = 0; j < currentRow.size(); j++)
                        {
                            Square currentSquare = currentRow.get(j);
                            TwoDimensionalVector currentPosition = currentSquare.GetPosition();

                            canvas.drawBitmap(currentSquare.GetImage(), currentPosition.GetX(), currentPosition.GetY(), paint);
                        }
                    }

                    gameActivity.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        });

        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolderolder)
    {
        if(thread != null)
        {
            thread.interrupt();
        }
    }
}
