package mobile.labs.acw.drag_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import mobile.labs.acw.objects.drag_square_object;
import mobile.labs.acw.objects.image_object;
import mobile.labs.acw.utilities.preferences;
import mobile.labs.acw.objects.puzzle_object;
import mobile.labs.acw.R;
import mobile.labs.acw.objects.square_object;
import mobile.labs.acw.objects.two_dimensional_vector_object;

public class drag_game extends SurfaceView implements SurfaceHolder.Callback
{
    private drag_game_activity dragGameActivity;

    private two_dimensional_vector_object canvasSize;

    private puzzle_object puzzle;
    private ArrayList<Bitmap> imageArray;
    private Bitmap cardBack, cardBackHighlighted;
    private Boolean selectionBoolean;

    private Integer highlightImage, cardImage, faceImage;

    private Integer maximumMatches;

    private Paint paint;

    private Integer offsetAmount;

    private two_dimensional_vector_object previousPosition;

    private square_object movingSquare;

    //Please see click_game for comments code is similar with minor changes
    public drag_game(Context context)
    {
        super(context);
    }

    //Please see click_game for comments code is similar with minor changes
    public drag_game(Context context, puzzle_object inPuzzle, ArrayList<image_object> inImageArray, RelativeLayout inRelativeLayout)
    {
        super(context);

        dragGameActivity = (drag_game_activity)context;

        puzzle = inPuzzle;

        imageArray = GetBitmapImageArray(inImageArray);

        canvasSize = new two_dimensional_vector_object(inRelativeLayout.getWidth(), inRelativeLayout.getHeight());

        Initialise();
    }

    //Please see click_game for comments code is similar with minor changes
    private ArrayList<Bitmap> GetBitmapImageArray(ArrayList<image_object> inImageArray)
    {
        ArrayList<Bitmap> imageArrayBitmapArray = new ArrayList<>();

        for(Integer i = 0; i < inImageArray.size(); i++)
        {
            imageArrayBitmapArray.add(inImageArray.get(i).GetBitmap());
        }

        return imageArrayBitmapArray;
    }

    //Please see click_game for comments code is similar with minor changes
    private void Initialise()
    {
        Integer rows = Integer.valueOf(puzzle.GetRows());
        Integer columns = puzzle.GetLayout().size() / rows;

        two_dimensional_vector_object size = new two_dimensional_vector_object((canvasSize.GetX() / columns), (canvasSize.GetY() / rows));

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

        if(preferences.GetAllInstancesOfKey(dragGameActivity, puzzle.GetId() + "Drag" + "square").size() > 0)
        {
            GetExistingSquares(rows, columns, size);
        }
        else
        {
            GetSquares(rows, columns, size);
        }

        selectionBoolean = false;

        highlightImage = -1;
        cardImage = 0;
        faceImage = 1;

        maximumMatches = (dragGameActivity.squares.get(0).size() * dragGameActivity.squares.size()) / 2;

        paint = new Paint();
        dragGameActivity.surfaceHolder = getHolder();
        dragGameActivity.surfaceHolder.addCallback(this);

        previousPosition = new two_dimensional_vector_object(-1, -1);
    }

    //Please see click_game for comments code is similar with minor changes
    private void SetScaledImageArray(two_dimensional_vector_object size)
    {
        Bitmap image;

        Integer sizeOne = size.GetX();
        Integer sizeTwo = size.GetY();

        two_dimensional_vector_object augmentedSize;

        if(sizeOne < sizeTwo)
        {
            offsetAmount = (sizeTwo - sizeOne) / 2;

            augmentedSize = new two_dimensional_vector_object(sizeOne, sizeOne);
        }
        else
        {
            offsetAmount = (sizeOne - sizeTwo) / 2;

            augmentedSize = new two_dimensional_vector_object(sizeTwo, sizeTwo);
        }

        for(int i = 0; i < imageArray.size(); i++)
        {
            image = imageArray.get(i);

            image = ScaledBitmap(image, augmentedSize);

            imageArray.set(i, image);
        }
    }

    //Please see click_game for comments code is similar with minor changes
    private Bitmap ScaledBitmap(Bitmap image, two_dimensional_vector_object size)
    {
        return Bitmap.createScaledBitmap(image, size.GetX(), size.GetY(), false);
    }

    //Please see click_game for comments code is similar with minor changes
    private void GetExistingSquares(Integer rows, Integer columns, two_dimensional_vector_object size)
    {
        for(Integer i = 0; i < rows; i++)
        {
            dragGameActivity.squares.add(new ArrayList<square_object>());

            String puzzleId = puzzle.GetId();

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = preferences.ReadInteger(dragGameActivity, puzzleId + "Drag" + "layout" + String.valueOf(i) + String.valueOf(j), -1);

                two_dimensional_vector_object position = new two_dimensional_vector_object((size.GetX() * j), (size.GetY() * i));

                Integer imageState = preferences.ReadInteger(dragGameActivity, puzzleId + "Drag" + "square" + String.valueOf(i) + String.valueOf(j), -2);

                switch(imageState)
                {
                    case -1:

                        dragGameActivity.squares.get(i).add(new square_object(cardBackHighlighted, imageState, layoutInteger, position));
                        break;

                    case 0:

                        dragGameActivity.squares.get(i).add(new square_object(cardBack, imageState, layoutInteger, position));
                        break;

                    case 1:

                        dragGameActivity.squares.get(i).add(new square_object(imageArray.get(layoutInteger), imageState, layoutInteger, position));
                        break;

                    default:

                        break;
                }
            }
        }

        Integer moveRow = dragGameActivity.moveSquare.GetSquare().GetX();
        Integer moveColumn = dragGameActivity.moveSquare.GetSquare().GetY();

        if(moveRow != -1 && moveColumn != -1)
        {
            dragGameActivity.moveSquare.SetPosition(dragGameActivity.squares.get(moveRow).get(moveColumn).GetPosition());
        }
    }

    //Please see click_game for comments code is similar with minor changes
    private void GetSquares(Integer rows, Integer columns, two_dimensional_vector_object size)
    {
        dragGameActivity.layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            dragGameActivity.squares.add(new ArrayList<square_object>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = dragGameActivity.layout.get((i * columns) + j) - 1;

                two_dimensional_vector_object position = new two_dimensional_vector_object((size.GetX() * j), (size.GetY() * i));

                dragGameActivity.squares.get(i).add(new square_object(imageArray.get(layoutInteger), faceImage, layoutInteger, position));
            }
        }
    }

    //Please see click_game for comments code is similar with minor changes
    private ArrayList<Integer> GetLayout(ArrayList<String> layout)
    {
        ArrayList<Integer> layoutIntegerArray = new ArrayList<>();

        for(Integer i = 0; i < layout.size(); i++)
        {
            layoutIntegerArray.add(Integer.valueOf(layout.get(i)));
        }

        return layoutIntegerArray;
    }

    //Please see click_game for comments code is similar with minor changes
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {

    }

    //Please see click_game for comments code is similar with minor changes
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        InitialiseThread();
    }

    //Please see click_game for comments code is similar with minor changes
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        dragGameActivity.KillThreads();
    }

    //Please see click_game for comments code is similar with minor changes
    private void InitialiseThread()
    {
        dragGameActivity.initialiseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(!dragGameActivity.firstBoolean)
                {
                    ThreadInitialise();

                    dragGameActivity.firstBoolean = true;
                }

                dragGameActivity.StartButton();

                DrawThread();
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
        });

        dragGameActivity.initialiseThread.start();
    }

    //Please see click_game for comments code is similar with minor changes
    private void DrawThread()
    {
        dragGameActivity.drawThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ThreadLoop();
            }

            private void ThreadLoop()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    if(dragGameActivity.surfaceHolder != null)
                    {
                        DrawOnCanvas();
                    }
                }
            }
        });

        dragGameActivity.drawThread.start();
    }

    //Please see click_game for comments code is similar with minor changes
    private void ChangeAllImages()
    {
        for(Integer i = 0; i < dragGameActivity.squares.size(); i++)
        {
            ArrayList<square_object> currentRow = dragGameActivity.squares.get(i);

            for(Integer j = 0; j < currentRow.size(); j++)
            {
                SetCardImageToCard(currentRow.get(j));
            }
        }
    }

    private void DrawOnCanvas()
    {
        try
        {
            Canvas canvas = dragGameActivity.surfaceHolder.lockCanvas();

            canvas.drawColor(ContextCompat.getColor(dragGameActivity, R.color.colorBackground));

            for(Integer i = 0; i < dragGameActivity.squares.size(); i++)
            {
                ArrayList<square_object> currentRow = dragGameActivity.squares.get(i);

                for(int j = 0; j < currentRow.size(); j++)
                {
                    square_object currentSquareObject = currentRow.get(j);
                    two_dimensional_vector_object currentPosition = currentSquareObject.GetPosition();
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

                //This draws the moving square on top of the other squares
                if(movingSquare != null)
                {
                    Bitmap movingSquareImage = movingSquare.GetImage();

                    if((movingSquareImage == cardBack || movingSquareImage == cardBackHighlighted))
                    {
                        two_dimensional_vector_object movingSquarePosition = movingSquare.GetPosition();

                        canvas.drawBitmap(movingSquare.GetImage(), movingSquarePosition.GetX(), movingSquarePosition.GetY(), paint);
                    }
                }

                two_dimensional_vector_object moveSquareSquare = dragGameActivity.moveSquare.GetSquare();
                Integer moveRow = moveSquareSquare.GetX();
                Integer moveColumn = moveSquareSquare.GetY();

                //This draws the moving square on top of the other squares
                if((moveRow != -1 && moveColumn != -1))
                {
                    square_object moveSquare = dragGameActivity.squares.get(moveRow).get(moveColumn);
                    Bitmap moveSquareImage = moveSquare.GetImage();

                    if((moveSquareImage == cardBack || moveSquareImage == cardBackHighlighted))
                    {
                        two_dimensional_vector_object moveSquarePosition = moveSquare.GetPosition();

                        canvas.drawBitmap(moveSquare.GetImage(), moveSquarePosition.GetX(), moveSquarePosition.GetY(), paint);

                    }
                }
            }

            dragGameActivity.surfaceHolder.unlockCanvasAndPost(canvas);
        }
        catch(Exception e)
        {
            dragGameActivity.KillThreads();
        }
    }

    public boolean onTouch(MotionEvent motionEvent)
    {
        Float x = motionEvent.getX();
        Float y = motionEvent.getY();

        Integer highlightedRow = dragGameActivity.highlightedSquare.GetX();
        Integer highlightedColumn = dragGameActivity.highlightedSquare.GetY();

        Integer moveRow = dragGameActivity.moveSquare.GetSquare().GetX();
        Integer moveColumn = dragGameActivity.moveSquare.GetSquare().GetY();

        //This responds to a down event
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(!selectionBoolean)
            {
                if ((highlightedRow == -1 || highlightedColumn == -1))
                {
                    BeginMove(x, y);
                }

                return true;
            }
        }

        //This responds to a move event
        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(!selectionBoolean)
            {
                if ((highlightedRow == -1 || highlightedColumn == -1) && (moveRow != -1 && moveColumn != -1))
                {
                    MoveSquare(x, y, moveRow, moveColumn);
                }

                return true;
            }
        }

        //This responds to a up event
        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            if(!selectionBoolean)
            {
                if((moveRow != -1 && moveColumn != -1))
                {
                    ActionUpEvent(x, y, highlightedRow, highlightedColumn, moveRow, moveColumn);
                }

                //This resets the positions for dragging a card
                if(previousPosition.GetX() != -1 || previousPosition.GetY() != -1)
                {
                    previousPosition = new two_dimensional_vector_object(-1, -1);
                }

                return true;
            }
        }

        return false;
    }

    //This initialises everything to begin moving squares
    private void BeginMove(Float x, Float y)
    {
        Integer length = dragGameActivity.squares.size();

        for(Integer i = 0; i < length; i++)
        {
            ArrayList<square_object> currentRow = dragGameActivity.squares.get(i);

            for(Integer j = 0; j < currentRow.size(); j++)
            {
                DownCheckEachSquare(x, y, i, j, currentRow);
            }
        }
    }

    //This sets the current square that is being dragged
    private void DownCheckEachSquare(Float x, Float y, Integer i, Integer j, ArrayList<square_object> currentRow)
    {
        square_object currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        two_dimensional_vector_object minimumPosition = currentSquare.GetPosition();
        Integer minimumX = minimumPosition.GetX();
        Integer minimumY = minimumPosition.GetY();
        Integer maximumX = minimumX + currentSquareImage.getWidth();
        Integer maximumY = minimumY + currentSquareImage.getHeight();

        if((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))
        {
            dragGameActivity.moveSquare.SetSquare(new two_dimensional_vector_object(i, j));
            dragGameActivity.moveSquare.SetPosition(minimumPosition);
        }
    }

    //This moves the current square being dragged to the distance between where the event is now and where it was before
    private void MoveSquare(Float x, Float y, Integer moveRow, Integer moveColumn)
    {
        square_object moveSquare = dragGameActivity.squares.get(moveRow).get(moveColumn);
        Bitmap moveSquareBitmap = moveSquare.GetImage();

        if(moveSquareBitmap == cardBack)
        {
            Integer previousPositionX = previousPosition.GetX();
            Integer previousPositionY = previousPosition.GetY();

            if(previousPositionX != -1 && previousPositionY != -1)
            {
                two_dimensional_vector_object moveSquarePosition = moveSquare.GetPosition();
                Integer moveSquarePositionX = moveSquarePosition.GetX();
                Integer moveSquarePositionY = moveSquarePosition.GetY();

                moveSquare.SetPosition(new two_dimensional_vector_object(Math.round(moveSquarePositionX + (x - previousPositionX)), Math.round(moveSquarePositionY + (y - previousPositionY))));
            }

            previousPosition = new two_dimensional_vector_object(Math.round(x), Math.round(y));
        }
    }

    //This is called on an action up event
    private void ActionUpEvent(Float x, Float y, Integer highlightedRow, Integer highlightedColumn, Integer moveRow, Integer moveColumn)
    {
        square_object moveSquare = dragGameActivity.squares.get(moveRow).get(moveColumn);
        Bitmap moveSquareBitmap = moveSquare.GetImage();
        two_dimensional_vector_object moveSquarePosition = dragGameActivity.moveSquare.GetPosition();
        Integer minimumX = moveSquarePosition.GetX();
        Integer minimumY = moveSquarePosition.GetY();
        Integer maximumX = minimumX + moveSquareBitmap.getWidth();
        Integer maximumY = minimumY + moveSquareBitmap.getHeight();

        Integer length = dragGameActivity.squares.size();

        if((highlightedRow == -1 || highlightedColumn == -1) && (!((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))))
        {
            selectionBoolean = true;

            SwapCheck(x, y, moveRow, moveColumn, length, moveSquare, moveSquarePosition);
        }
        else
        {
            moveSquare.SetPosition(dragGameActivity.moveSquare.GetPosition());

            SelectionCheck(x, y, highlightedRow, highlightedColumn, length);
        }
    }

    //This checks to see if two squares should be swapped
    private void SwapCheck(Float x, Float y, Integer moveRow, Integer moveColumn, Integer length, square_object moveSquare, two_dimensional_vector_object moveSquarePosition)
    {
        for (Integer i = 0; i < length; i++)
        {
            ArrayList<square_object> currentRow = dragGameActivity.squares.get(i);

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

    //This checks to see if the current square should be swapped
    private Boolean UpSwapCheckEachSquare(Float x, Float y, Integer moveRow, Integer moveColumn, square_object moveSquare, two_dimensional_vector_object moveSquarePosition, Integer i, Integer j, ArrayList<square_object> currentRow)
    {
        square_object currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        two_dimensional_vector_object minimumPosition = currentSquare.GetPosition();
        Integer minimumX = minimumPosition.GetX();
        Integer minimumY = minimumPosition.GetY();
        Integer maximumX = minimumX + currentSquareImage.getWidth();
        Integer maximumY = minimumY + currentSquareImage.getHeight();

        if (currentSquare != moveSquare && (currentSquareImage == cardBack || currentSquareImage == cardBackHighlighted))
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

    //This checks to see if the cards are adjacent to each other
    private boolean CheckAdjacentConnection(Integer currentSquareX, Integer currentSquareY, Integer selectionSquareX, Integer selectionSquareY)
    {
        return selectionSquareX.equals(currentSquareX) && ((selectionSquareY == (currentSquareY + 1)) || (selectionSquareY == (currentSquareY - 1))) || (selectionSquareY.equals(currentSquareY) && ((selectionSquareX == (currentSquareX + 1)) || (selectionSquareX == (currentSquareX - 1))));
    }

    //This moves the two squares to eachothers positions and then moves them back again
    private void SquareMoveThread(final square_object square, final two_dimensional_vector_object startPosition, final two_dimensional_vector_object endPosition, final two_dimensional_vector_object resetPosition)
    {
        final Thread currentThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap squareImage = square.GetImage();

                //This stops disallowed swaps
                if(squareImage == cardBack || squareImage == cardBackHighlighted)
                {
                    MoveSquare(square, startPosition, endPosition);
                }

                //This resets positions
                square.SetPosition(resetPosition);

                Reset(Thread.currentThread());
            }

            //This moves the square from its previous position to its destined location
            private void MoveSquare(square_object square, two_dimensional_vector_object startPosition, two_dimensional_vector_object endPosition)
            {
                two_dimensional_vector_object moveAmount = new two_dimensional_vector_object((endPosition.GetX() - startPosition.GetX()) / 30, (endPosition.GetY() - startPosition.GetY()) / 30);

                for(int i = 0; i < 30; i++)
                {
                    two_dimensional_vector_object position = square.GetPosition();

                    square.SetPosition(new two_dimensional_vector_object((position.GetX() + moveAmount.GetX()), (position.GetY() + moveAmount.GetY())));

                    try
                    {
                        Thread.sleep(8);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        dragGameActivity.moveThreads.add(currentThread);

        currentThread.start();
    }

    //This resets all variables associated with the click events
    private void Reset(Thread currentThread)
    {
        if(movingSquare != null)
        {
            movingSquare = null;
        }

        two_dimensional_vector_object square = dragGameActivity.moveSquare.GetSquare();
        two_dimensional_vector_object position = dragGameActivity.moveSquare.GetPosition();

        if(square.GetX() != -1 || square.GetY() != -1 || position.GetX() != -1 || position.GetY() != -1)
        {
            dragGameActivity.moveSquare = new drag_square_object(new two_dimensional_vector_object(-1, -1), new two_dimensional_vector_object(-1, -1));
        }

        if(previousPosition.GetX() != -1 || previousPosition.GetY() != -1)
        {
            previousPosition = new two_dimensional_vector_object(-1, -1);
        }

        if(selectionBoolean)
        {
            selectionBoolean = false;
        }

        dragGameActivity.moveThreads.remove(currentThread);
    }

    private void SelectionCheck(Float x, Float y, Integer highlightedRow, Integer highlightedColumn, Integer length)
    {
        for (int i = 0; i < length; i++)
        {
            ArrayList<square_object> currentRow = dragGameActivity.squares.get(i);

            for (int j = 0; j < currentRow.size(); j++)
            {
                if (UpSelectionCheckEachSquare(x, y, highlightedRow, highlightedColumn, length, i, j, currentRow))
                {
                    return;
                }
            }
        }
    }

    //Please see click_game for comments code is similar with minor changes
    private Boolean UpSelectionCheckEachSquare(Float x, Float y, Integer highlightedRow, Integer highlightedColumn, Integer length, Integer i, Integer j, ArrayList<square_object> currentRow)
    {
        square_object currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        two_dimensional_vector_object minimumPosition = currentSquare.GetPosition();
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

    //Please see click_game for comments code is similar with minor changes
    private void FirstSelection(Integer i, Integer j, square_object currentSquare)
    {
        SetCardImageToHighlight(currentSquare);

        dragGameActivity.highlightedSquare.SetX(i);
        dragGameActivity.highlightedSquare.SetY(j);
    }

    //Please see click_game for comments code is similar with minor changes
    private void ResetSelection(square_object currentSquare)
    {
        SetCardImageToCard(currentSquare);

        ResetSquares();
    }

    //Please see click_game for comments code is similar with minor changes
    private Boolean SecondSelectionCheck(Integer i, Integer j, square_object currentSquare, Integer highlightedRow, Integer highlightedColumn, Integer length)
    {
        selectionBoolean = true;

        square_object highlightedSquare = dragGameActivity.squares.get(highlightedRow).get(highlightedColumn);

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
                else
                {
                    selectionBoolean = false;

                    return false;
                }
            }
        }

        return false;
    }

    //Please see click_game for comments code is similar with minor changes
    private void ResetSquares()
    {
        dragGameActivity.highlightedSquare.SetX(-1);
        dragGameActivity.highlightedSquare.SetY(-1);
    }

    //Please see click_game for comments code is similar with minor changes
    private Boolean CheckAndUpdateMatch(square_object currentSquareObject, square_object highlightedSquare)
    {
        Integer highlightSquareImagePosition = highlightedSquare.GetImagePosition();
        Integer currentSquareImagePosition = currentSquareObject.GetImagePosition();

        SetSquaresToLayoutImages(currentSquareObject, currentSquareImagePosition, highlightedSquare, highlightSquareImagePosition);

        if(highlightSquareImagePosition.equals(currentSquareImagePosition))
        {
            UpdateScoreAttempts();

            selectionBoolean = false;

            return Return(true);
        }
        else
        {
            UpdateScore();

            ToResetSquaresImages(highlightedSquare, currentSquareObject);

            return Return(false);
        }
    }

    //Please see click_game for comments code is similar with minor changes
    private void SetSquaresToLayoutImages(square_object currentSquareObject, Integer currentSquareImagePosition, square_object highlightedSquare, Integer highlightSquareImagePosition)
    {
        SetSquareToLayoutImage(highlightedSquare, highlightSquareImagePosition);
        SetSquareToLayoutImage(currentSquareObject, currentSquareImagePosition);
    }

    //Please see click_game for comments code is similar with minor changes
    private void SetSquareToLayoutImage(square_object squareObject, Integer position)
    {
        SetCardImageToFace(squareObject, position);
    }

    //Please see click_game for comments code is similar with minor changes
    private void ToResetSquaresImages(final square_object highlightedSquare, final square_object currentSquareObject)
    {
        dragGameActivity.updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ResetSquaresImages(highlightedSquare, currentSquareObject);
            }

            private void ResetSquaresImages(square_object previouslyHighlightedSquareObject, square_object currentSquareObject)
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

                selectionBoolean = false;
            }
        });

        dragGameActivity.updateThread.start();
    }

    //Please see click_game for comments code is similar with minor changes
    private void ResetSquareImage(square_object squareObject)
    {
        SetCardImageToCard(squareObject);
    }

    //Please see click_game for comments code is similar with minor changes
    private void SetCardImageToHighlight(square_object squareObject)
    {
        squareObject.SetImage(cardBackHighlighted);
        squareObject.SetImageState(highlightImage);
    }

    //Please see click_game for comments code is similar with minor changes
    private void SetCardImageToCard(square_object squareObject)
    {
        squareObject.SetImage(cardBack);
        squareObject.SetImageState(cardImage);
    }

    //Please see click_game for comments code is similar with minor changes
    private void SetCardImageToFace(square_object squareObject, Integer position)
    {
        squareObject.SetImage(imageArray.get(position));
        squareObject.SetImageState(faceImage);
    }

    //Please see click_game for comments code is similar with minor changes
    private void UpdateScoreAttempts()
    {
        dragGameActivity.correctAttempts++;

        UpdateScore();
    }

    //Please see click_game for comments code is similar with minor changes
    private void UpdateScore()
    {
        dragGameActivity.attempts++;

        Float scoreRatio = Float.valueOf(dragGameActivity.correctAttempts) / Float.valueOf(dragGameActivity.attempts);

        dragGameActivity.score = Math.round(scoreRatio * 100);

        dragGameActivity.SetScoreTextView();
    }

    //Please see click_game for comments code is similar with minor changes
    private Boolean Return(Boolean returnBool)
    {
        ResetSquares();

        return returnBool;
    }
}