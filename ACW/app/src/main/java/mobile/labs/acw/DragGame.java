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

public class DragGame extends SurfaceView implements SurfaceHolder.Callback
{
    private DragGameActivity dragGameActivity;

    private TwoDimensionalVectorObject canvasSize;

    private PuzzleObject puzzle;
    private ArrayList<Bitmap> imageArray;
    private Bitmap cardBack, cardBackHighlighted;

    private Integer maximumMatches;

    private Integer highlightImage, cardImage, faceImage;

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

        if(Preferences.GetAllInstancesOfKey(dragGameActivity, "square").size() > 0)
        {
            GetExistingSquares(rows, columns, size);
        }
        else
        {
            GetSquares(rows, columns, size);
        }

        maximumMatches = (dragGameActivity.squares.get(0).size() * dragGameActivity.squares.size()) / 2;

        highlightImage = -1;
        cardImage = 0;
        faceImage = 1;

        paint = new Paint();
        dragGameActivity.surfaceHolder = getHolder();
        dragGameActivity.surfaceHolder.addCallback(this);
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
            dragGameActivity.squares.add(new ArrayList<SquareObject>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                TwoDimensionalVectorObject position = new TwoDimensionalVectorObject((size.GetX() * j), (size.GetY() * i));

                Integer imageState = Preferences.ReadInteger(dragGameActivity, "square" + String.valueOf(i) + String.valueOf(j), -2);

                switch(imageState)
                {
                    case -1:

                        dragGameActivity.squares.get(i).add(new SquareObject(cardBackHighlighted, imageState, layoutInteger, position));
                        break;

                    case 0:

                        dragGameActivity.squares.get(i).add(new SquareObject(cardBack, imageState, layoutInteger, position));
                        break;

                    case 1:

                        dragGameActivity.squares.get(i).add(new SquareObject(imageArray.get(layoutInteger), imageState, layoutInteger, position));
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
            dragGameActivity.squares.add(new ArrayList<SquareObject>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                TwoDimensionalVectorObject position = new TwoDimensionalVectorObject((size.GetX() * j), (size.GetY() * i));

                dragGameActivity.squares.get(i).add(new SquareObject(imageArray.get(layoutInteger), faceImage, layoutInteger, position));
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
                if(!dragGameActivity.firstBoolean)
                {
                    ThreadInitialise();

                    dragGameActivity.firstBoolean = true;
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
        for(int i = 0; i < dragGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                SetCardImageToCard(currentRow.get(j));
            }
        }
    }

    private void DrawOnCanvas()
    {
        Canvas canvas = dragGameActivity.surfaceHolder.lockCanvas();

        canvas.drawColor(Color.parseColor("#000000"));

        for(int i = 0; i < dragGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                SquareObject currentSquareObject = currentRow.get(j);
                TwoDimensionalVectorObject currentPosition = currentSquareObject.GetPosition();

                canvas.drawBitmap(currentSquareObject.GetImage(), currentPosition.GetX(), currentPosition.GetY(), paint);
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

    public boolean onTouch(MotionEvent motionEvent)
    {
        Float x = motionEvent.getX();
        Float y = motionEvent.getY();

        Integer row = dragGameActivity.highlightedSquare.GetX();
        Integer column = dragGameActivity.highlightedSquare.GetY();

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && (row == -1 || column == -1))
        {
            return BeginMove(x, y);
        }

        return motionEvent.getAction() == MotionEvent.ACTION_UP && ActionDownEvent(x, y, row, column);
    }

    private Boolean BeginMove(Float x, Float y)
    {
        Integer length = dragGameActivity.squares.size();

        for(int i = 0; i < length; i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

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
            return true;
        }

        return false;
    }

    private Boolean ActionDownEvent(Float x, Float y, Integer row, Integer column)
    {
        Integer length = dragGameActivity.squares.size();

        for(int i = 0; i < length; i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                if(CheckEachSquare(x, y, row, column, i, j, currentRow, length))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private Boolean CheckEachSquare(Float x, Float y, Integer row, Integer column, Integer i, Integer j, ArrayList<SquareObject> currentRow, Integer length)
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
                        return SecondSelection(i, j,currentSquare, row, column, length);
                    }
                }
            }
        }

        return false;
    }

    private Boolean FirstSelection(Integer i, Integer j, SquareObject currentSquare)
    {
        SetCardImageToHighlight(currentSquare);

        dragGameActivity.highlightedSquare.SetX(i);
        dragGameActivity.highlightedSquare.SetY(j);

        return true;
    }

    private Boolean ResetSelection(SquareObject currentSquare)
    {
        SetCardImageToCard(currentSquare);

        ResetHighlightedSquare();

        return true;
    }

    private Boolean SecondSelection(Integer i, Integer j, SquareObject currentSquare, Integer row, Integer column, Integer length)
    {
        SquareObject highlightedSquare = dragGameActivity.squares.get(row).get(column);

        for(Integer k = 0; k < length; k++)
        {
            Integer highlightedSquareColumn = dragGameActivity.squares.get(k).indexOf(highlightedSquare);

            if(highlightedSquareColumn != -1)
            {
                if(CheckAdjacentConnection(i, j, k, highlightedSquareColumn))
                {
                    if (CheckAndUpdateMatch(currentSquare, highlightedSquare))
                    {
                        dragGameActivity.currentMatches++;

                        if (dragGameActivity.currentMatches.equals(maximumMatches))
                        {
                            dragGameActivity.OnGameFinished();
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void ResetHighlightedSquare()
    {
        dragGameActivity.highlightedSquare.SetX(-1);
        dragGameActivity.highlightedSquare.SetY(-1);
    }

    private boolean CheckAdjacentConnection(int currentSquareX, int currentSquareY, Integer highlightedSquareX, Integer highlightedSquareY)
    {
        return highlightedSquareX == currentSquareX && ((highlightedSquareY == (currentSquareY + 1)) || (highlightedSquareY == (currentSquareY - 1))) || (highlightedSquareY == currentSquareY && ((highlightedSquareX == (currentSquareX + 1)) || (highlightedSquareX == (currentSquareX - 1))));
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
        ResetHighlightedSquare();

        return returnBool;
    }
}