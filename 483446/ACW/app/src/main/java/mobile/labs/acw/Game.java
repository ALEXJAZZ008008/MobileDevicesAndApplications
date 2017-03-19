package mobile.labs.acw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Game extends SurfaceView implements SurfaceHolder.Callback
{
    private GameActivity gameActivity;

    private PuzzleObject puzzle;
    private ArrayList<Bitmap> imageArray;
    private Bitmap cardBack, cardBackHighlighted;

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

        cardBack = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.card), size);
        cardBackHighlighted = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.inverted_card), size);

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

                squares.get(i).add(new Square(ScaledBitmap(imageArray.get(layoutInteger), size), layoutInteger, position));
            }
        }
    }

    private Bitmap ScaledBitmap(Bitmap image, TwoDimensionalVector size)
    {
        return Bitmap.createScaledBitmap(image, size.GetX(), size.GetY(), false);
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
                ThreadInitialise();

                ThreadLoop();
            }

            private void ThreadInitialise()
            {
                DrawOnCanvas();

                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                ChangeAllImages();
            }

            private void ChangeAllImages()
            {
                for(int i = 0; i < squares.size(); i++)
                {
                    ArrayList<Square> currentRow = squares.get(i);

                    for(int j = 0; j < currentRow.size(); j++)
                    {
                        currentRow.get(j).SetImage(cardBack);
                    }
                }
            }

            private void ThreadLoop()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    DrawOnCanvas();
                }
            }

            private void DrawOnCanvas()
            {
                Canvas canvas = gameActivity.surfaceHolder.lockCanvas();

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

    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        Float x = motionEvent.getX();
        Float y = motionEvent.getY();

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            for(int i = 0; i < squares.size(); i++)
            {
                ArrayList<Square> currentRow = squares.get(i);

                for(int j = 0; j < currentRow.size(); j++)
                {
                    Square currentSquare = currentRow.get(j);
                    Bitmap currentSquareImage = currentSquare.GetImage();
                    TwoDimensionalVector minimumPosition = currentSquare.GetPosition();
                    Integer minimumX = minimumPosition.GetX();
                    Integer minimumY = minimumPosition.GetY();
                    Integer maximumX = minimumX + currentSquareImage.getWidth();
                    Integer maximumY = minimumY + currentSquareImage.getHeight();

                    if((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))
                    {
                        currentSquare.SetImage(cardBackHighlighted);

                        return true;
                    }
                }
            }
        }

        return false;
    }
}
