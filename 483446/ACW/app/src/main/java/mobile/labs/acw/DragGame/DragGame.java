package mobile.labs.acw.DragGame;

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

import mobile.labs.acw.Objects.DragSquareObject;
import mobile.labs.acw.Objects.ImageObject;
import mobile.labs.acw.Utilities.Preferences;
import mobile.labs.acw.Objects.PuzzleObject;
import mobile.labs.acw.R;
import mobile.labs.acw.Objects.SquareObject;
import mobile.labs.acw.Objects.TwoDimensionalVectorObject;

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

    private Integer offsetAmount;

    private SquareObject movingSquare;

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

        if(canvasSize.GetX() < canvasSize.GetY())
        {
            cardBack = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.card), size);
            cardBackHighlighted = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.inverted_card), size);
        }
        else
        {
            cardBack = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.card_rotated), size);
            cardBackHighlighted = ScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.inverted_card_rotated), size);
        }

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

        Integer sizeOne = size.GetX();
        Integer sizeTwo = size.GetY();

        TwoDimensionalVectorObject augmentedSize;

        if(sizeOne < sizeTwo)
        {
            offsetAmount = (sizeTwo - sizeOne) / 2;

            augmentedSize = new TwoDimensionalVectorObject(sizeOne, sizeOne);
        }
        else
        {
            offsetAmount = (sizeOne - sizeTwo) / 2;

            augmentedSize = new TwoDimensionalVectorObject(sizeTwo, sizeTwo);
        }

        for(int i = 0; i < imageArray.size(); i++)
        {
            image = imageArray.get(i);

            image = ScaledBitmap(image, augmentedSize);

            imageArray.set(i, image);
        }
    }

    private Bitmap ScaledBitmap(Bitmap image, TwoDimensionalVectorObject size)
    {
        return Bitmap.createScaledBitmap(image, size.GetX(), size.GetY(), false);
    }

    private void GetExistingSquares(Integer rows, Integer columns, TwoDimensionalVectorObject size)
    {
        for(Integer i = 0; i < rows; i++)
        {
            dragGameActivity.squares.add(new ArrayList<SquareObject>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = Preferences.ReadInteger(dragGameActivity, "layout" + String.valueOf(i) + String.valueOf(j), -1);

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
        dragGameActivity.layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            dragGameActivity.squares.add(new ArrayList<SquareObject>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = dragGameActivity.layout.get((i * columns) + j) - 1;

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
        for(Integer i = 0; i < dragGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for(Integer j = 0; j < currentRow.size(); j++)
            {
                SetCardImageToCard(currentRow.get(j));
            }
        }
    }

    private void DrawOnCanvas()
    {
        Canvas canvas = dragGameActivity.surfaceHolder.lockCanvas();

        canvas.drawColor(Color.parseColor("#FFFFFF"));

        for(Integer i = 0; i < dragGameActivity.squares.size(); i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                SquareObject currentSquareObject = currentRow.get(j);
                TwoDimensionalVectorObject currentPosition = currentSquareObject.GetPosition();
                Bitmap currentSquareObjectImage = currentSquareObject.GetImage();
                Integer currentPositionX = currentPosition.GetX();
                Integer currentPositionY = currentPosition.GetY();

                if(currentSquareObjectImage != cardBack && currentSquareObjectImage != cardBackHighlighted)
                {
                    if(canvasSize.GetX() < canvasSize.GetY())
                    {
                        currentPositionY += offsetAmount;
                    }
                    else
                    {
                        currentPositionX += offsetAmount;
                    }
                }

                canvas.drawBitmap(currentSquareObject.GetImage(), currentPositionX, currentPositionY, paint);
            }

            if(movingSquare != null)
            {
                Bitmap movingSquareImage = movingSquare.GetImage();

                if((movingSquareImage == cardBack || movingSquareImage == cardBackHighlighted))
                {
                    TwoDimensionalVectorObject movingSquarePosition = movingSquare.GetPosition();

                    canvas.drawBitmap(movingSquare.GetImage(), movingSquarePosition.GetX(), movingSquarePosition.GetY(), paint);
                }
            }

            TwoDimensionalVectorObject moveSquareSquare = dragGameActivity.moveSquare.getSquare();
            Integer moveRow = moveSquareSquare.GetX();
            Integer moveColumn = moveSquareSquare.GetY();

            if((moveRow != -1 && moveColumn != -1))
            {
                SquareObject moveSquare = dragGameActivity.squares.get(moveRow).get(moveColumn);
                Bitmap moveSquareImage = moveSquare.GetImage();

                if((moveSquareImage == cardBack || moveSquareImage == cardBackHighlighted))
                {
                    TwoDimensionalVectorObject moveSquarePosition = moveSquare.GetPosition();

                    canvas.drawBitmap(moveSquare.GetImage(), moveSquarePosition.GetX(), moveSquarePosition.GetY(), paint);

                }
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

        Integer highlightedRow = dragGameActivity.highlightedSquare.GetX();
        Integer highlightedColumn = dragGameActivity.highlightedSquare.GetY();

        Integer moveRow = dragGameActivity.moveSquare.getSquare().GetX();
        Integer moveColumn = dragGameActivity.moveSquare.getSquare().GetY();

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            if((highlightedRow == -1 || highlightedColumn == -1))
            {
                BeginMove(x, y);
            }

            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            if((highlightedRow == -1 || highlightedColumn == -1) && (moveRow != -1 && moveColumn != -1))
            {
                MoveSquare(x, y, moveRow, moveColumn);
            }

            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            ActionUpEvent(x, y, highlightedRow, highlightedColumn, moveRow, moveColumn);

            return true;
        }

        return false;
    }

    private void BeginMove(Float x, Float y)
    {
        Integer length = dragGameActivity.squares.size();

        for(Integer i = 0; i < length; i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for(Integer j = 0; j < currentRow.size(); j++)
            {
                DownCheckEachSquare(x, y, i, j, currentRow);
            }
        }
    }

    private void DownCheckEachSquare(Float x, Float y, Integer i, Integer j, ArrayList<SquareObject> currentRow)
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
            dragGameActivity.moveSquare.setSquare(new TwoDimensionalVectorObject(i, j));
            dragGameActivity.moveSquare.setPosition(minimumPosition);
        }
    }

    private void MoveSquare(Float x, Float y, Integer moveRow, Integer moveColumn)
    {
        SquareObject moveSquare = dragGameActivity.squares.get(moveRow).get(moveColumn);
        Bitmap moveSquareBitmap = moveSquare.GetImage();

        if(moveSquareBitmap == cardBack)
        {
            moveSquare.SetPosition(new TwoDimensionalVectorObject(Math.round(x - (moveSquareBitmap.getWidth() / 2)), Math.round(y - (moveSquareBitmap.getHeight() / 2))));
        }
    }

    private void ActionUpEvent(Float x, Float y, Integer highlightedRow, Integer highlightedColumn, Integer moveRow, Integer moveColumn)
    {
        SquareObject moveSquare = dragGameActivity.squares.get(moveRow).get(moveColumn);
        Bitmap moveSquareBitmap = moveSquare.GetImage();
        TwoDimensionalVectorObject moveSquarePosition = dragGameActivity.moveSquare.getPosition();
        Integer minimumX = moveSquarePosition.GetX();
        Integer minimumY = moveSquarePosition.GetY();
        Integer maximumX = minimumX + moveSquareBitmap.getWidth();
        Integer maximumY = minimumY + moveSquareBitmap.getHeight();

        Integer length = dragGameActivity.squares.size();

        if((highlightedRow == -1 || highlightedColumn == -1) && (!((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))))
        {
            SwapCheck(x, y, moveRow, moveColumn, length, moveSquare, moveSquarePosition);
        }
        else
        {
            moveSquare.SetPosition(dragGameActivity.moveSquare.getPosition());

            SelectionCheck(x, y, highlightedRow, highlightedColumn, length);
        }
    }

    private void SwapCheck(Float x, Float y, Integer moveRow, Integer moveColumn, Integer length, SquareObject moveSquare, TwoDimensionalVectorObject moveSquarePosition)
    {
        for (Integer i = 0; i < length; i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for (Integer j = 0; j < currentRow.size(); j++)
            {
                if (UpSwapCheckEachSquare(x, y, moveRow, moveColumn, moveSquare, moveSquarePosition, i, j, currentRow))
                {
                    return;
                }
            }
        }

        SquareMoveThread(moveSquare, moveSquare.GetPosition(), moveSquarePosition, moveSquarePosition);
    }

    private Boolean UpSwapCheckEachSquare(Float x, Float y, Integer moveRow, Integer moveColumn, SquareObject moveSquare, TwoDimensionalVectorObject moveSquarePosition, Integer i, Integer j, ArrayList<SquareObject> currentRow)
    {
        SquareObject currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        TwoDimensionalVectorObject minimumPosition = currentSquare.GetPosition();
        Integer minimumX = minimumPosition.GetX();
        Integer minimumY = minimumPosition.GetY();
        Integer maximumX = minimumX + currentSquareImage.getWidth();
        Integer maximumY = minimumY + currentSquareImage.getHeight();

        if (currentSquare != moveSquare)
        {
            if(CheckAdjacentConnection(i, j, moveRow, moveColumn))
            {
                if ((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))
                {
                    Integer moveSquareImagePosition = moveSquare.GetImagePosition();

                    moveSquare.SetImagePosition(currentSquare.GetImagePosition());
                    currentSquare.SetImagePosition(moveSquareImagePosition);

                    movingSquare = currentSquare;

                    SquareMoveThread(moveSquare, moveSquare.GetPosition(), minimumPosition, moveSquarePosition);
                    SquareMoveThread(currentSquare, minimumPosition, moveSquarePosition, minimumPosition);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean CheckAdjacentConnection(Integer currentSquareX, Integer currentSquareY, Integer selectionSquareX, Integer selectionSquareY)
    {
        return selectionSquareX.equals(currentSquareX) && ((selectionSquareY == (currentSquareY + 1)) || (selectionSquareY == (currentSquareY - 1))) || (selectionSquareY.equals(currentSquareY) && ((selectionSquareX == (currentSquareX + 1)) || (selectionSquareX == (currentSquareX - 1))));
    }

    private void SquareMoveThread(final SquareObject square, final TwoDimensionalVectorObject startPosition, final TwoDimensionalVectorObject endPosition, final TwoDimensionalVectorObject resetPosition)
    {
        Thread moveThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                MoveSquare(square, startPosition, endPosition, resetPosition);
            }

            private void MoveSquare(SquareObject square, TwoDimensionalVectorObject startPosition, TwoDimensionalVectorObject endPosition, TwoDimensionalVectorObject resetPosition)
            {
                TwoDimensionalVectorObject moveAmount = new TwoDimensionalVectorObject((endPosition.GetX() - startPosition.GetX()) / 30, (endPosition.GetY() - startPosition.GetY()) / 30);

                for(int i = 0; i < 30; i++)
                {
                    try
                    {
                        Thread.sleep(16);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    TwoDimensionalVectorObject position = square.GetPosition();

                    square.SetPosition(new TwoDimensionalVectorObject((position.GetX() + moveAmount.GetX()), (position.GetY() + moveAmount.GetY())));
                }

                try
                {
                    Thread.sleep(16);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                square.SetPosition(resetPosition);

                DragSquareObject nullDragSquare = new DragSquareObject(new TwoDimensionalVectorObject(-1, -1), new TwoDimensionalVectorObject(-1, -1));

                if(dragGameActivity.moveSquare != nullDragSquare)
                {
                    dragGameActivity.moveSquare = nullDragSquare;
                }

                if(movingSquare != null)
                {
                    movingSquare = null;
                }
            }
        });

        moveThread.start();
    }

    private void SelectionCheck(Float x, Float y, Integer highlightedRow, Integer highlightedColumn, Integer length)
    {
        for (int i = 0; i < length; i++)
        {
            ArrayList<SquareObject> currentRow = dragGameActivity.squares.get(i);

            for (int j = 0; j < currentRow.size(); j++)
            {
                if (UpSelectionCheckEachSquare(x, y, highlightedRow, highlightedColumn, length, i, j, currentRow))
                {
                    return;
                }
            }
        }
    }

    private Boolean UpSelectionCheckEachSquare(Float x, Float y, Integer highlightedRow, Integer highlightedColumn, Integer length, Integer i, Integer j, ArrayList<SquareObject> currentRow)
    {
        SquareObject currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        TwoDimensionalVectorObject minimumPosition = currentSquare.GetPosition();
        Integer minimumX = minimumPosition.GetX();
        Integer minimumY = minimumPosition.GetY();
        Integer maximumX = minimumX + currentSquareImage.getWidth();
        Integer maximumY = minimumY + currentSquareImage.getHeight();

        if ((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))
        {
            if ((highlightedRow == -1 || highlightedColumn == -1) && currentSquareImage == cardBack)
            {
                FirstSelection(i, j, currentSquare);

                return true;
            }
            else
            {
                if (currentSquareImage == cardBackHighlighted)
                {
                    ResetSelection(currentSquare);

                    return true;
                }
                else
                {
                    if (currentSquareImage == cardBack)
                    {
                        return SecondSelectionCheck(i, j, currentSquare, highlightedRow, highlightedColumn, length);
                    }
                }
            }
        }

        return false;
    }

    private void FirstSelection(Integer i, Integer j, SquareObject currentSquare)
    {
        SetCardImageToHighlight(currentSquare);

        dragGameActivity.highlightedSquare.SetX(i);
        dragGameActivity.highlightedSquare.SetY(j);
    }

    private void ResetSelection(SquareObject currentSquare)
    {
        SetCardImageToCard(currentSquare);

        ResetSquares();
    }

    private Boolean SecondSelectionCheck(Integer i, Integer j, SquareObject currentSquare, Integer highlightedRow, Integer highlightedColumn, Integer length)
    {
        SquareObject highlightedSquare = dragGameActivity.squares.get(highlightedRow).get(highlightedColumn);

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

    private void ResetSquares()
    {
        dragGameActivity.highlightedSquare.SetX(-1);
        dragGameActivity.highlightedSquare.SetY(-1);
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
        ResetSquares();

        return returnBool;
    }
}