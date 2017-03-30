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

        //Scales images and adds offsets to locations
        SetScaledImageArray(size);

        //This sets the correct orientation images for the orientation of the device
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

        //This checks for existing cards and sets them accordingly
        if(preferences.GetAllInstancesOfKey(clickGameActivity, puzzle.GetId() + "Click" + "square").size() > 0)
        {
            GetExistingSquares(rows, columns, size);
        }
        else
        {
            GetSquares(rows, columns, size);
        }

        //Sets some member variable initial states
        selectionBoolean = false;

        highlightImage = -1;
        cardImage = 0;
        faceImage = 1;

        maximumMatches = (clickGameActivity.squares.get(0).size() * clickGameActivity.squares.size()) / 2;

        paint = new Paint();
        clickGameActivity.surfaceHolder = getHolder();
        clickGameActivity.surfaceHolder.addCallback(this);
    }

    //Scales images and adds offsets to locations
    private void SetScaledImageArray(two_dimensional_vector_object size)
    {
        Bitmap image;

        //These variables are used to avoid accessing the same piece of data twice
        Integer sizeOne = size.GetX();
        Integer sizeTwo = size.GetY();

        two_dimensional_vector_object augmentedSize;

        //These set the relevant offsets etc depending upon the orientation of the device etc
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

        //This sets each image in the array
        for(int i = 0; i < imageArray.size(); i++)
        {
            image = imageArray.get(i);

            image = ScaledBitmap(image, augmentedSize);

            imageArray.set(i, image);
        }
    }

    //This scales bitmaps
    private Bitmap ScaledBitmap(Bitmap image, two_dimensional_vector_object size)
    {
        return Bitmap.createScaledBitmap(image, size.GetX(), size.GetY(), false);
    }

    //This gets the existing squares
    private void GetExistingSquares(Integer rows, Integer columns, two_dimensional_vector_object size)
    {
        //These variables are used to avoid accessing the same piece of data twice
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        //This is for the rows of the squares
        for(Integer i = 0; i < rows; i++)
        {
            clickGameActivity.squares.add(new ArrayList<square_object>());

            //This is for the columns of the squares
            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                two_dimensional_vector_object position = new two_dimensional_vector_object((size.GetX() * j), (size.GetY() * i));

                //This reads saved information
                Integer imageState = preferences.ReadInteger(clickGameActivity, puzzle.GetId() + "Click" + "square" + String.valueOf(i) + String.valueOf(j), -2);

                //This gets the current state of the square
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

    //This starts new squares
    private void GetSquares(Integer rows, Integer columns, two_dimensional_vector_object size)
    {
        ArrayList<Integer> layout = GetLayout(puzzle.GetLayout());

        //This is for the rows of the squares
        for(Integer i = 0; i < rows; i++)
        {
            clickGameActivity.squares.add(new ArrayList<square_object>());

            //This is for the columns of the squares
            for(Integer j = 0; j < columns; j++)
            {
                Integer layoutInteger = layout.get((i * columns) + j) - 1;

                two_dimensional_vector_object position = new two_dimensional_vector_object((size.GetX() * j), (size.GetY() * i));

                clickGameActivity.squares.get(i).add(new square_object(imageArray.get(layoutInteger), faceImage, layoutInteger, position));
            }
        }
    }

    //This gets the layout of the puzzle
    private ArrayList<Integer> GetLayout(ArrayList<String> layout)
    {
        ArrayList<Integer> layoutIntegerArray = new ArrayList<>();

        for(Integer i = 0; i < layout.size(); i++)
        {
            layoutIntegerArray.add(Integer.valueOf(layout.get(i)));
        }

        return layoutIntegerArray;
    }

    //This is not used but is required to operate
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {

    }

    //This is called when this is created
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        //This calls the initialise thread
        InitialiseThread();
    }

    //This is called when this is created
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        //This kills all threads
        clickGameActivity.KillThreads();
    }

    //This is the initialise thread
    private void InitialiseThread()
    {
        clickGameActivity.initialiseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //This checks to see if it the first time this puzzle has been called
                if(!clickGameActivity.firstBoolean)
                {
                    ThreadInitialise();

                    clickGameActivity.firstBoolean = true;
                }

                //This starts the reset button
                clickGameActivity.StartButton();

                //This starts the draw thread
                DrawThread();
            }

            private void ThreadInitialise()
            {
                //This draws the current squares on the canvas
                DrawOnCanvas();

                //This pauses the thread for one second
                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //This swaps all images to face down
                ChangeAllImages();
            }
        });

        //This starts the thread
        clickGameActivity.initialiseThread.start();
    }

    //This is the draw thread
    private void DrawThread()
    {
        clickGameActivity.drawThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ThreadLoop();
            }

            //This is the main loop for the thread
            private void ThreadLoop()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    if(clickGameActivity.surfaceHolder != null )
                    {
                        //This draws all the images on the canvas
                        DrawOnCanvas();
                    }
                }
            }
        });

        //This starts the thread
        clickGameActivity.drawThread.start();
    }

    //This swaps all the images to be face down
    private void ChangeAllImages()
    {
        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<square_object> currentRow = clickGameActivity.squares.get(i);

            for(int j = 0; j < currentRow.size(); j++)
            {
                //This swaps one image to be face down
                SetCardImageToCard(currentRow.get(j));
            }
        }
    }

    //This draws the current state of all squares
    private void DrawOnCanvas()
    {
        try
        {
            //This creates and then locks the canvas
            Canvas canvas = clickGameActivity.surfaceHolder.lockCanvas();

            //This sets the background to be white
            canvas.drawColor(ContextCompat.getColor(clickGameActivity, R.color.colorBackground));

            //This is for all the rows of squares
            for(int i = 0; i < clickGameActivity.squares.size(); i++)
            {
                ArrayList<square_object> currentRow = clickGameActivity.squares.get(i);

                //This is for all the columns of squares
                for(int j = 0; j < currentRow.size(); j++)
                {
                    //These variables are used to avoid accessing the same piece of data twice
                    square_object currentSquareObject = currentRow.get(j);
                    two_dimensional_vector_object currentPosition = currentSquareObject.GetPosition();
                    Bitmap currentSquareObjectImage = currentSquareObject.GetImage();
                    Integer currentPositionX = currentPosition.GetX();
                    Integer currentPositionY = currentPosition.GetY();

                    //This applys the correct amount of offset to each card if it needs it
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

                    //This draws the one card
                    canvas.drawBitmap(currentSquareObject.GetImage(), currentPositionX, currentPositionY, paint);
                }
            }

            //This unlocks the canvas
            clickGameActivity.surfaceHolder.unlockCanvasAndPost(canvas);
        }
        catch(Exception e)
        {
            //This kills all the threads
            clickGameActivity.KillThreads();
        }
    }

    //This is called whenever something touches the screen of the device
    public boolean onTouch(MotionEvent motionEvent)
    {
        //These variables are used to avoid accessing the same piece of data twice
        Float x = motionEvent.getX();
        Float y = motionEvent.getY();

        //This is called when the touch come down upon the screen
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            //This stops clicks for one second after a selection has been made
            if(!selectionBoolean)
            {
                //This is what to do on the down of the touch
                ActionDownEvent(x, y);

                return true;
            }
        }

        return false;
    }

    //This is what to do on the down of the touch
    private void ActionDownEvent(Float x, Float y)
    {
        //This is for all the rows of squares
        for(int i = 0; i < clickGameActivity.squares.size(); i++)
        {
            ArrayList<square_object> currentRow = clickGameActivity.squares.get(i);

            //This is for all the columns of squares
            for(int j = 0; j < currentRow.size(); j++)
            {
                //The logic for each part actually occurs within the bool
                if(CheckEachSquare(x, y, i, j, currentRow))
                {
                    return;
                }
            }
        }
    }

    private Boolean CheckEachSquare(Float x, Float y, Integer i, Integer j, ArrayList<square_object> currentRow)
    {
        //These variables are used to avoid accessing the same piece of data twice
        square_object currentSquare = currentRow.get(j);
        Bitmap currentSquareImage = currentSquare.GetImage();
        two_dimensional_vector_object minimumPosition = currentSquare.GetPosition();
        Integer minimumX = minimumPosition.GetX();
        Integer minimumY = minimumPosition.GetY();
        Integer maximumX = minimumX + currentSquareImage.getWidth();
        Integer maximumY = minimumY + currentSquareImage.getHeight();

        //This checks to see if the touch is within that square
        if((x >= minimumX && x <= maximumX) && (y >= minimumY && y <= maximumY))
        {
            //These variables are used to avoid accessing the same piece of data twice
            Integer row = clickGameActivity.highlightedSquare.GetX();
            Integer column = clickGameActivity.highlightedSquare.GetY();

            //This is true when a valid card is clicked for the first time
            if((row == -1 || column == -1) && currentSquareImage == cardBack)
            {
                //This is what to do when a card is clicked first
                FirstSelection(i, j, currentSquare);

                return true;
            }
            else
            {
                //This is true when the same card is clicked to deselect it
                if(currentSquareImage == cardBackHighlighted)
                {
                    ResetSelection(currentSquare);

                    return true;
                }
                else
                {
                    //This is true when it is the second click event and the card is valid
                    if (currentSquareImage == cardBack)
                    {
                        //This bool contains all of the logic for the second click
                        return SecondSelectionCheck(currentSquare, row, column);
                    }
                }
            }
        }

        return false;
    }

    //This is what to do when a card is clicked first
    private void FirstSelection(Integer i, Integer j, square_object currentSquare)
    {
        //This makes the card highlighted
        SetCardImageToHighlight(currentSquare);

        //This saves the highlighted square
        clickGameActivity.highlightedSquare.SetX(i);
        clickGameActivity.highlightedSquare.SetY(j);
    }

    //This resets the highlighted square to its original state
    private void ResetSelection(square_object currentSquare)
    {
        SetCardImageToCard(currentSquare);

        ResetHighlightedSquare();
    }

    //This bool contains all of the logic for the second click
    private Boolean SecondSelectionCheck(square_object currentSquare, Integer row, Integer column)
    {
        //This stops all subsequent clicks for a time
        selectionBoolean = true;

        //These variables are used to avoid accessing the same piece of data twice
        square_object highlightedSquare = clickGameActivity.squares.get(row).get(column);

        //The logic for each part actually occurs within the bool
        if(CheckAndUpdateMatch(currentSquare, highlightedSquare))
        {
            //This updates the current number of matches
            clickGameActivity.currentMatches++;

            //This ends the game
            if(clickGameActivity.currentMatches.equals(maximumMatches))
            {
                clickGameActivity.OnGameFinished();
            }

            return true;
        }

        return false;
    }

    //This resets the highlighted square
    private void ResetHighlightedSquare()
    {
        clickGameActivity.highlightedSquare.SetX(-1);
        clickGameActivity.highlightedSquare.SetY(-1);
    }

    //This contains all of the logic for matching tiles
    private Boolean CheckAndUpdateMatch(square_object currentSquareObject, square_object highlightedSquare)
    {
        //These variables are used to avoid accessing the same piece of data twice
        Integer highlightSquareImagePosition = highlightedSquare.GetImagePosition();
        Integer currentSquareImagePosition = currentSquareObject.GetImagePosition();

        //This turns the squares over
        SetSquaresToLayoutImages(currentSquareObject, currentSquareImagePosition, highlightedSquare, highlightSquareImagePosition);

        if(highlightSquareImagePosition.equals(currentSquareImagePosition))
        {
            //This updates the score
            UpdateScoreAttempts();

            //This allows subsequent clicks
            selectionBoolean = false;

            //This returns true
            return Return(true);
        }
        else
        {
            //This updates the score
            UpdateScore();

            //This turns the squares over
            ToResetSquaresImages(highlightedSquare, currentSquareObject);

            //This returns false
            return Return(false);
        }
    }

    //This turns the squares over
    private void SetSquaresToLayoutImages(square_object currentSquareObject, Integer currentSquareImagePosition, square_object highlightedSquare, Integer highlightSquareImagePosition)
    {
        SetSquareToLayoutImage(highlightedSquare, highlightSquareImagePosition);
        SetSquareToLayoutImage(currentSquareObject, currentSquareImagePosition);
    }

    //This turns the square over
    private void SetSquareToLayoutImage(square_object squareObject, Integer position)
    {
        SetCardImageToFace(squareObject, position);
    }

    //This turns the squares over for one second
    private void ToResetSquaresImages(final square_object highlightedSquare, final square_object currentSquareObject)
    {
        clickGameActivity.updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ResetSquaresImages(highlightedSquare, currentSquareObject);
            }

            private void ResetSquaresImages(square_object previouslyHighlightedSquareObject, square_object currentSquareObject)
            {
                //This pauses for a second
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                //This resets the squares
                ResetSquareImage(previouslyHighlightedSquareObject);
                ResetSquareImage(currentSquareObject);

                //This allows subsequent clicks
                selectionBoolean = false;
            }
        });

        //This starts the threas
        clickGameActivity.updateThread.start();
    }

    //This turns the square over
    private void ResetSquareImage(square_object squareObject)
    {
        SetCardImageToCard(squareObject);
    }

    //This highlights the card
    private void SetCardImageToHighlight(square_object squareObject)
    {
        squareObject.SetImage(cardBackHighlighted);
        squareObject.SetImageState(highlightImage);
    }

    //This sets the card images to the card image
    private void SetCardImageToCard(square_object squareObject)
    {
        squareObject.SetImage(cardBack);
        squareObject.SetImageState(cardImage);
    }

    //This turns the card over
    private void SetCardImageToFace(square_object squareObject, Integer position)
    {
        squareObject.SetImage(imageArray.get(position));
        squareObject.SetImageState(faceImage);
    }

    //This updates the score
    private void UpdateScoreAttempts()
    {
        //This updates correct attempts
        clickGameActivity.correctAttempts++;

        UpdateScore();
    }

    //This updates the score
    private void UpdateScore()
    {
        //This updates all attempts
        clickGameActivity.attempts++;

        //This calculates the current score as a percentage
        Float scoreRatio = Float.valueOf(clickGameActivity.correctAttempts) / Float.valueOf(clickGameActivity.attempts);

        //This updates the stored score
        clickGameActivity.score = Math.round(scoreRatio * 100);

        //This caps the score
        if(clickGameActivity.score > 75)
        {
            clickGameActivity.score = 75;
        }

        //This displays the score
        clickGameActivity.SetScoreTextView();
    }

    //This returns
    private Boolean Return(Boolean returnBool)
    {
        ResetHighlightedSquare();

        return returnBool;
    }
}
