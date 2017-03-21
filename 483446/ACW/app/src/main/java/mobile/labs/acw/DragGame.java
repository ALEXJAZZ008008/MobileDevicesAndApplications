package mobile.labs.acw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class DragGame extends SurfaceView implements SurfaceHolder.Callback
{
    private DragGameActivity dragGameActivity;

    private PuzzleObject puzzle;
    private ArrayList<Bitmap> imageArray;
    private Bitmap cardBack, cardBackHighlighted;
    private Square highlightedSquare;

    private Integer maximumMatches, currentMatches;

    private TwoDimensionalVector canvasSize;

    private ArrayList<ArrayList<Square>> squares;

    private Paint paint;
    private Thread drawThread, updateThread;

    public DragGame(Context context)
    {
        super(context);
    }

    public DragGame(Context context, PuzzleObject inPuzzle, ArrayList<ImageObject> inImageArray, RelativeLayout inRelativeLayout)
    {
        super(context);

        dragGameActivity = (DragGameActivity)context;

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

        SetScaledImageArray(size);

        cardBack = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.card), size);
        cardBackHighlighted = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.inverted_card), size);

        squares = new ArrayList<>();

        GetSquares(rows, columns, size);

        maximumMatches = (squares.get(0).size() * squares.size()) / 2;
        currentMatches = 0;

        paint = new Paint();
        dragGameActivity.surfaceHolder = getHolder();
        dragGameActivity.surfaceHolder.addCallback(this);
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

                squares.get(i).add(new Square(imageArray.get(layoutInteger), layoutInteger, position));
            }
        }
    }

    private void SetScaledImageArray(TwoDimensionalVector size)
    {
        Bitmap image;

        for(int i = 0; i < imageArray.size(); i++)
        {
            image = imageArray.get(i);

            image = ScaledBitmap(image, size);

            imageArray.set(i, image);
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
        drawThread = new Thread(new Runnable()
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

            private void ThreadLoop()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    DrawOnCanvas();
                }
            }
        });

        drawThread.start();
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

    private void DrawOnCanvas()
    {
        Canvas canvas = dragGameActivity.surfaceHolder.lockCanvas();

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

        dragGameActivity.surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolderolder)
    {
        if(drawThread != null)
        {
            drawThread.interrupt();
        }

        if(updateThread != null)
        {
            updateThread.interrupt();
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
                        if(highlightedSquare == null && currentSquareImage == cardBack)
                        {
                            currentSquare.SetImage(cardBackHighlighted);

                            highlightedSquare = currentSquare;

                            return true;
                        }
                        else
                        {
                            if(currentSquare == highlightedSquare)
                            {
                                currentSquare.SetImage(cardBack);

                                highlightedSquare = null;
                            }
                            else
                            {
                                if (currentSquareImage == cardBack)
                                {
                                    if(CheckMatch(currentSquare))
                                    {
                                        currentMatches++;

                                        if(currentMatches.equals(maximumMatches))
                                        {
                                            dragGameActivity.OnGameFinished();
                                        }

                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private Boolean CheckMatch(final Square currentSquare)
    {
        Integer highlightSquareImagePosition = highlightedSquare.GetImagePosition();
        Integer currentSquareImagePosition = currentSquare.GetImagePosition();

        SetSquaresToLayoutImages(currentSquare, highlightSquareImagePosition, currentSquareImagePosition);

        if(highlightSquareImagePosition.equals(currentSquareImagePosition))
        {
            UpdateScoreClicks();

            return Return(true);
        }
        else
        {
            UpdateScore();

            ToResetSquaresImages(highlightedSquare, currentSquare);

            return Return(false);
        }
    }

    private void SetSquaresToLayoutImages(Square currentSquare, Integer highlightSquareImagePosition, Integer currentSquareImagePosition)
    {
        SetSquareToLayoutImage(highlightedSquare, highlightSquareImagePosition);
        SetSquareToLayoutImage(currentSquare, currentSquareImagePosition);
    }

    private void SetSquareToLayoutImage(Square square, Integer position)
    {
        square.SetImage(imageArray.get(position));
    }

    private void ToResetSquaresImages(final Square previouslyHighlightedSquare, final Square currentSquare)
    {
        updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ResetSquaresImages(previouslyHighlightedSquare, currentSquare);
            }

            private void ResetSquaresImages(Square previouslyHighlightedSquare, Square currentSquare)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                ResetSquareImage(previouslyHighlightedSquare);
                ResetSquareImage(currentSquare);
            }
        });

        updateThread.start();
    }

    private void ResetSquareImage(Square square)
    {
        square.SetImage(cardBack);
    }

    private void UpdateScoreClicks()
    {
        dragGameActivity.correctAttempts++;

        UpdateScore();
    }

    private void UpdateScore()
    {
        dragGameActivity.attempts++;

        Float scoreRatio = Float.valueOf(dragGameActivity.correctAttempts) / Float.valueOf(dragGameActivity.attempts);

        dragGameActivity.score = Math.round(scoreRatio * 100);

        dragGameActivity.SetScoreTextView();
    }

    private Boolean Return(Boolean returnBool)
    {
        highlightedSquare = null;

        return returnBool;
    }
}
