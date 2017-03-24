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
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ClickGame extends SurfaceView implements SurfaceHolder.Callback
{
    private ClickGameActivity clickGameActivity;

    private TwoDimensionalVectorObject canvasSize;

    private PuzzleObject puzzle;
    private ArrayList<Bitmap> imageArray;
    private Bitmap cardBack, cardBackHighlighted;

    private Integer maximumMatches;

    private Integer highlightImage, cardImage, faceImage;

    private Paint paint;
    private Thread drawThread, updateThread;

    public ClickGame(Context context)
    {
        super(context);
    }

    public ClickGame(Context context, PuzzleObject inPuzzle, ArrayList<ImageObject> inImageArray, RelativeLayout inRelativeLayout)
    {
        super(context);

        clickGameActivity = (ClickGameActivity)context;

        puzzle = inPuzzle;

        imageArray = GetBitmapImageArray(inImageArray);

        canvasSize = new TwoDimensionalVectorObject(inRelativeLayout.getWidth(), inRelativeLayout.getHeight());

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

        TwoDimensionalVectorObject size = new TwoDimensionalVectorObject((canvasSize.GetX() / columns), (canvasSize.GetY() / rows));

        SetScaledImageArray(size);

        cardBack = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.card), size);
        cardBackHighlighted = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.inverted_card), size);

        if(Preferences.GetAllInstancesOfKey(clickGameActivity, "square").size() > 0)
        {
            GetExistingSquares(rows, columns, size);
        }
        else
        {
            GetSquares(rows, columns, size);
        }

        maximumMatches = (clickGameActivity.squares.get(0).size() * clickGameActivity.squares.size()) / 2;

        highlightImage = -1;
        cardImage = 0;
        faceImage = 1;

        paint = new Paint();
        clickGameActivity.surfaceHolder = getHolder();
        clickGameActivity.surfaceHolder.addCallback(this);
    }

    private void SetScaledImageArray(TwoDimensionalVectorObject size)
    {
        Bitmap image;

        for(int i = 0; i < imageArray.size(); i++)
        {
            image = imageArray.get(i);

            image = ScaledBitmap(image, size);

            imageArray.set(i, image);
        }
    }

    private Bitmap ScaledBitmap(Bitmap image, TwoDimensionalVectorObject size)
    {
        return Bitmap.createScaledBitmap(image, size.GetX(), size.GetY(), false);
    }

    private void GetExistingSquares(Integer rows, Integer columns, TwoDimensionalVectorObject size)
    {
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            clickGameActivity.squares.add(new ArrayList<SquareObject>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                TwoDimensionalVectorObject position = new TwoDimensionalVectorObject((size.GetX() * j), (size.GetY() * i));

                Integer imageState = Preferences.ReadInteger(clickGameActivity, "square" + String.valueOf(i) + String.valueOf(j), -2);

                switch(imageState)
                {
                    case -1:

                        clickGameActivity.squares.get(i).add(new SquareObject(cardBackHighlighted, imageState, layoutInteger, position));
                        break;

                    case 0:

                        clickGameActivity.squares.get(i).add(new SquareObject(cardBack, imageState, layoutInteger, position));
                        break;

                    case 1:

                        clickGameActivity.squares.get(i).add(new SquareObject(imageArray.get(layoutInteger), imageState, layoutInteger, position));
                        break;

                    default:

                        break;
                }
            }
        }
    }

    private void GetSquares(Integer rows, Integer columns, TwoDimensionalVectorObject size)
    {
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            clickGameActivity.squares.add(new ArrayList<SquareObject>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                TwoDimensionalVectorObject position = new TwoDimensionalVectorObject((size.GetX() * j), (size.GetY() * i));

                clickGameActivity.squares.get(i).add(new SquareObject(imageArray.get(layoutInteger), faceImage, layoutInteger, position));
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
        drawThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(!clickGameActivity.firstBoolean)
                {
                    ThreadInitialise();

                    clickGameActivity.firstBoolean = true;
                }

                ThreadLoop();
            }

            private void ThreadInitialise()
            {
                DrawOnCanvas();

                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception e)
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
        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = clickGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                SetCardImageToCard(currentRow.get(j));
            }
        }
    }

    private void DrawOnCanvas()
    {
        Canvas canvas = clickGameActivity.surfaceHolder.lockCanvas();

        canvas.drawColor(Color.parseColor("#000000"));

        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = clickGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                SquareObject currentSquareObject = currentRow.get(j);
                TwoDimensionalVectorObject currentPosition = currentSquareObject.GetPosition();

                canvas.drawBitmap(currentSquareObject.GetImage(), currentPosition.GetX(), currentPosition.GetY(), paint);
            }
        }

        clickGameActivity.surfaceHolder.unlockCanvasAndPost(canvas);
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

    public boolean onTouch(MotionEvent motionEvent)
    {
        Float x = motionEvent.getX();
        Float y = motionEvent.getY();

        return motionEvent.getAction() == MotionEvent.ACTION_DOWN && ActionDownEvent(x, y);
    }

    private Boolean ActionDownEvent(Float x, Float y)
    {
        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = clickGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                if(CheckEachSquare(x, y, i, j, currentRow))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private Boolean CheckEachSquare(Float x, Float y, Integer i, Integer j, ArrayList<SquareObject> currentRow)
    {
        SquareObject currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        TwoDimensionalVectorObject minimumPosition = currentSquare.GetPosition();
        Integer minimumX = minimumPosition.GetX();
        Integer minimumY = minimumPosition.GetY();
        Integer maximumX = minimumX + currentSquareImage.getWidth();
        Integer maximumY = minimumY + currentSquareImage.getHeight();

        if((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))
        {
            Integer row = clickGameActivity.highlightedSquare.GetX();
            Integer column = clickGameActivity.highlightedSquare.GetY();

            if((row == -1 || column == -1) && currentSquareImage == cardBack)
            {
                return FirstSelection(i, j, currentSquare);
            }
            else
            {
                if(currentSquareImage == cardBackHighlighted)
                {
                    return ResetSelection(currentSquare);
                }
                else
                {
                    if (currentSquareImage == cardBack)
                    {
                        return SecondSelection(currentSquare, row, column);
                    }
                }
            }
        }

        return false;
    }

    private Boolean FirstSelection(Integer i, Integer j, SquareObject currentSquare)
    {
        SetCardImageToHighlight(currentSquare);

        clickGameActivity.highlightedSquare.SetX(i);
        clickGameActivity.highlightedSquare.SetY(j);

        return true;
    }

    private Boolean ResetSelection(SquareObject currentSquare)
    {
        SetCardImageToCard(currentSquare);

        ResetHighlightedSquare();

        return true;
    }

    private Boolean SecondSelection(SquareObject currentSquare, Integer row, Integer column)
    {
        SquareObject highlightedSquare = clickGameActivity.squares.get(row).get(column);

        if(CheckAndUpdateMatch(currentSquare, highlightedSquare))
        {
            clickGameActivity.currentMatches++;

            if(clickGameActivity.currentMatches.equals(maximumMatches))
            {
                clickGameActivity.OnGameFinished();
            }

            return true;
        }

        return false;
    }

    private void ResetHighlightedSquare()
    {
        clickGameActivity.highlightedSquare.SetX(-1);
        clickGameActivity.highlightedSquare.SetY(-1);
    }

    private Boolean CheckAndUpdateMatch(SquareObject currentSquareObject, SquareObject highlightedSquare)
    {
        Integer highlightSquareImagePosition = highlightedSquare.GetImagePosition();
        Integer currentSquareImagePosition = currentSquareObject.GetImagePosition();

        SetSquaresToLayoutImages(currentSquareObject, currentSquareImagePosition, highlightedSquare, highlightSquareImagePosition);

        if(highlightSquareImagePosition.equals(currentSquareImagePosition))
        {
            UpdateScoreAttempts();

            return Return(true);
        }
        else
        {
            UpdateScore();

            ToResetSquaresImages(highlightedSquare, currentSquareObject);

            return Return(false);
        }
    }

    private void SetSquaresToLayoutImages(SquareObject currentSquareObject, Integer currentSquareImagePosition, SquareObject highlightedSquare, Integer highlightSquareImagePosition)
    {
        SetSquareToLayoutImage(highlightedSquare, highlightSquareImagePosition);
        SetSquareToLayoutImage(currentSquareObject, currentSquareImagePosition);
    }

    private void SetSquareToLayoutImage(SquareObject squareObject, Integer position)
    {
        SetCardImageToFace(squareObject, position);
    }

    private void ToResetSquaresImages(final SquareObject highlightedSquare, final SquareObject currentSquareObject)
    {
        updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ResetSquaresImages(highlightedSquare, currentSquareObject);
            }

            private void ResetSquaresImages(SquareObject previouslyHighlightedSquareObject, SquareObject currentSquareObject)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                ResetSquareImage(previouslyHighlightedSquareObject);
                ResetSquareImage(currentSquareObject);
            }
        });

        updateThread.start();
    }

    private void ResetSquareImage(SquareObject squareObject)
    {
        SetCardImageToCard(squareObject);
    }

    private void SetCardImageToHighlight(SquareObject squareObject)
    {
        squareObject.SetImage(cardBackHighlighted);
        squareObject.SetImageState(highlightImage);
    }

    private void SetCardImageToCard(SquareObject squareObject)
    {
        squareObject.SetImage(cardBack);
        squareObject.SetImageState(cardImage);
    }

    private void SetCardImageToFace(SquareObject squareObject, Integer position)
    {
        squareObject.SetImage(imageArray.get(position));
        squareObject.SetImageState(faceImage);
    }

    private void UpdateScoreAttempts()
    {
        clickGameActivity.correctAttempts++;

        UpdateScore();
    }

    private void UpdateScore()
    {
        clickGameActivity.attempts++;

        Float scoreRatio = Float.valueOf(clickGameActivity.correctAttempts) / Float.valueOf(clickGameActivity.attempts);

        clickGameActivity.score = Math.round(scoreRatio * 100);

        clickGameActivity.SetScoreTextView();
    }

    private Boolean Return(Boolean returnBool)
    {
        ResetHighlightedSquare();

        return returnBool;
    }
}
