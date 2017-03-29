package mobile.labs.acw.click_game;

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
import mobile.labs.acw.objects.image_object;
import mobile.labs.acw.utilities.preferences;
import mobile.labs.acw.objects.puzzle_object;
import mobile.labs.acw.R;
import mobile.labs.acw.objects.square_object;
import mobile.labs.acw.objects.two_dimensional_vector_object;

public class click_game extends SurfaceView implements SurfaceHolder.Callback
{
    private click_game_activity clickGameActivity;

    private two_dimensional_vector_object canvasSize;

    private puzzle_object puzzle;
    private ArrayList<Bitmap> imageArray;
    private Bitmap cardBack, cardBackHighlighted;
    private Boolean selectionBoolean;

    private Integer highlightImage, cardImage, faceImage;

    private Integer maximumMatches;

    private Paint paint;

    private Integer offsetAmount;

    //Default constructor for click_game
    public click_game(Context context)
    {
        super(context);
    }

    //Constructor for click game which builds a game
    public click_game(Context context, puzzle_object inPuzzle, ArrayList<image_object> inImageArray, RelativeLayout inRelativeLayout)
    {
        super(context);

        clickGameActivity = (click_game_activity)context;

        puzzle = inPuzzle;

        imageArray = GetBitmapImageArray(inImageArray);

        canvasSize = new two_dimensional_vector_object(inRelativeLayout.getWidth(), inRelativeLayout.getHeight());

        //Initialises variables
        Initialise();
    }

    //This transposes the given image_object list into the containing bitmap list and then returns the list
    private ArrayList<Bitmap> GetBitmapImageArray(ArrayList<image_object> inImageArray)
    {
        ArrayList<Bitmap> imageArrayBitmapArray = new ArrayList<>();

        for(Integer i = 0; i < inImageArray.size(); i++)
        {
            imageArrayBitmapArray.add(inImageArray.get(i).GetBitmap());
        }

        return imageArrayBitmapArray;
    }

    //This initialises most of the member variables
    private void Initialise()
    {
        //These variables are used to avoid accessing the same piece of data twice
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

        if(preferences.GetAllInstancesOfKey(clickGameActivity, puzzle.GetId() + "Click" + "square").size() > 0)
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

        maximumMatches = (clickGameActivity.squares.get(0).size() * clickGameActivity.squares.size()) / 2;

        paint = new Paint();
        clickGameActivity.surfaceHolder = getHolder();
        clickGameActivity.surfaceHolder.addCallback(this);
    }

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

    private Bitmap ScaledBitmap(Bitmap image, two_dimensional_vector_object size)
    {
        return Bitmap.createScaledBitmap(image, size.GetX(), size.GetY(), false);
    }

    private void GetExistingSquares(Integer rows, Integer columns, two_dimensional_vector_object size)
    {
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            clickGameActivity.squares.add(new ArrayList<square_object>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                two_dimensional_vector_object position = new two_dimensional_vector_object((size.GetX() * j), (size.GetY() * i));

                Integer imageState = preferences.ReadInteger(clickGameActivity, puzzle.GetId() + "Click" + "square" + String.valueOf(i) + String.valueOf(j), -2);

                switch(imageState)
                {
                    case -1:

                        clickGameActivity.squares.get(i).add(new square_object(cardBackHighlighted, imageState, layoutInteger, position));
                        break;

                    case 0:

                        clickGameActivity.squares.get(i).add(new square_object(cardBack, imageState, layoutInteger, position));
                        break;

                    case 1:

                        clickGameActivity.squares.get(i).add(new square_object(imageArray.get(layoutInteger), imageState, layoutInteger, position));
                        break;

                    default:

                        break;
                }
            }
        }
    }

    private void GetSquares(Integer rows, Integer columns, two_dimensional_vector_object size)
    {
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        for(Integer i = 0; i < rows; i++)
        {
            clickGameActivity.squares.add(new ArrayList<square_object>());

            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                two_dimensional_vector_object position = new two_dimensional_vector_object((size.GetX() * j), (size.GetY() * i));

                clickGameActivity.squares.get(i).add(new square_object(imageArray.get(layoutInteger), faceImage, layoutInteger, position));
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
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        InitialiseThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        clickGameActivity.KillThreads();
    }

    private void InitialiseThread()
    {
        clickGameActivity.initialiseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(!clickGameActivity.firstBoolean)
                {
                    ThreadInitialise();

                    clickGameActivity.firstBoolean = true;
                }

                clickGameActivity.StartButton();

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

        clickGameActivity.initialiseThread.start();
    }

    private void DrawThread()
    {
        clickGameActivity.drawThread = new Thread(new Runnable()
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
                    if(clickGameActivity.surfaceHolder != null )
                    {
                        DrawOnCanvas();
                    }
                }
            }
        });

        clickGameActivity.drawThread.start();
    }

    private void ChangeAllImages()
    {
        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<square_object> currentRow = clickGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                SetCardImageToCard(currentRow.get(j));
            }
        }
    }

    private void DrawOnCanvas()
    {
        try
        {
            Canvas canvas = clickGameActivity.surfaceHolder.lockCanvas();

            canvas.drawColor(ContextCompat.getColor(clickGameActivity, R.color.colorBackground));

            for(int i = 0; i < clickGameActivity.squares.size(); i++)
            {
                ArrayList<square_object> currentRow = clickGameActivity.squares.get(i);

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
            }

            clickGameActivity.surfaceHolder.unlockCanvasAndPost(canvas);
        }
        catch(Exception e)
        {
            clickGameActivity.KillThreads();
        }
    }

    public boolean onTouch(MotionEvent motionEvent)
    {
        Float x = motionEvent.getX();
        Float y = motionEvent.getY();

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(!selectionBoolean)
            {
                ActionDownEvent(x, y);

                return true;
            }
        }

        return false;
    }

    private void ActionDownEvent(Float x, Float y)
    {
        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<square_object> currentRow = clickGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                if(CheckEachSquare(x, y, i, j, currentRow))
                {
                    return;
                }
            }
        }
    }

    private Boolean CheckEachSquare(Float x, Float y, Integer i, Integer j, ArrayList<square_object> currentRow)
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
            Integer row = clickGameActivity.highlightedSquare.GetX();
            Integer column = clickGameActivity.highlightedSquare.GetY();

            if((row == -1 || column == -1) && currentSquareImage == cardBack)
            {
                FirstSelection(i, j, currentSquare);

                return true;
            }
            else
            {
                if(currentSquareImage == cardBackHighlighted)
                {
                    ResetSelection(currentSquare);

                    return true;
                }
                else
                {
                    if (currentSquareImage == cardBack)
                    {
                        return SecondSelectionCheck(currentSquare, row, column);
                    }
                }
            }
        }

        return false;
    }

    private void FirstSelection(Integer i, Integer j, square_object currentSquare)
    {
        SetCardImageToHighlight(currentSquare);

        clickGameActivity.highlightedSquare.SetX(i);
        clickGameActivity.highlightedSquare.SetY(j);
    }

    private void ResetSelection(square_object currentSquare)
    {
        SetCardImageToCard(currentSquare);

        ResetHighlightedSquare();
    }

    private Boolean SecondSelectionCheck(square_object currentSquare, Integer row, Integer column)
    {
        selectionBoolean = true;

        square_object highlightedSquare = clickGameActivity.squares.get(row).get(column);

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

    private Boolean CheckAndUpdateMatch(square_object currentSquareobject, square_object highlightedSquare)
    {
        Integer highlightSquareImagePosition = highlightedSquare.GetImagePosition();
        Integer currentSquareImagePosition = currentSquareobject.GetImagePosition();

        SetSquaresToLayoutImages(currentSquareobject, currentSquareImagePosition, highlightedSquare, highlightSquareImagePosition);

        if(highlightSquareImagePosition.equals(currentSquareImagePosition))
        {
            UpdateScoreAttempts();

            selectionBoolean = false;

            return Return(true);
        }
        else
        {
            UpdateScore();

            ToResetSquaresImages(highlightedSquare, currentSquareobject);

            return Return(false);
        }
    }

    private void SetSquaresToLayoutImages(square_object currentSquareobject, Integer currentSquareImagePosition, square_object highlightedSquare, Integer highlightSquareImagePosition)
    {
        SetSquareToLayoutImage(highlightedSquare, highlightSquareImagePosition);
        SetSquareToLayoutImage(currentSquareobject, currentSquareImagePosition);
    }

    private void SetSquareToLayoutImage(square_object squareobject, Integer position)
    {
        SetCardImageToFace(squareobject, position);
    }

    private void ToResetSquaresImages(final square_object highlightedSquare, final square_object currentSquareObject)
    {
        clickGameActivity.updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ResetSquaresImages(highlightedSquare, currentSquareObject);
            }

            private void ResetSquaresImages(square_object previouslyHighlightedSquareobject, square_object currentSquareobject)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                ResetSquareImage(previouslyHighlightedSquareobject);
                ResetSquareImage(currentSquareobject);

                selectionBoolean = false;
            }
        });

        clickGameActivity.updateThread.start();
    }

    private void ResetSquareImage(square_object squareObject)
    {
        SetCardImageToCard(squareObject);
    }

    private void SetCardImageToHighlight(square_object squareObject)
    {
        squareObject.SetImage(cardBackHighlighted);
        squareObject.SetImageState(highlightImage);
    }

    private void SetCardImageToCard(square_object squareObject)
    {
        squareObject.SetImage(cardBack);
        squareObject.SetImageState(cardImage);
    }

    private void SetCardImageToFace(square_object squareObject, Integer position)
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

        if(clickGameActivity.score > 75)
        {
            clickGameActivity.score = 75;
        }

        clickGameActivity.SetScoreTextView();
    }

    private Boolean Return(Boolean returnBool)
    {
        ResetHighlightedSquare();

        return returnBool;
    }
}
