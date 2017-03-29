package mobile.labs.acw.drag_game;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import mobile.labs.acw.objects.image_object;
import mobile.labs.acw.objects.drag_square_object;
import mobile.labs.acw.utilities.preferences;
import mobile.labs.acw.objects.puzzle_object;
import mobile.labs.acw.R;
import mobile.labs.acw.objects.square_object;
import mobile.labs.acw.objects.two_dimensional_vector_object;

public class drag_game_activity extends Activity
{
    public puzzle_object puzzle;

    public ArrayList<image_object> imageArray;
    public Integer length;

    public drag_game dragGame;
    public RelativeLayout relativeLayout;

    public SurfaceHolder surfaceHolder;

    public Thread initialiseThread, drawThread, updateThread;
    public ArrayList<Thread> moveThreads;

    public Integer score, attempts, correctAttempts;
    private TextView textView;
    private Button resetButton;

    public ArrayList<ArrayList<square_object>> squares;
    public ArrayList<Integer> layout;
    public two_dimensional_vector_object highlightedSquare;
    public drag_square_object moveSquare;
    public Integer currentMatches;
    public Boolean firstBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Initialise();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        GetPreferences();

        SetScoreTextView();
    }

    @Override
    protected void onPause()
    {
        if(firstBoolean)
        {
            SavePreferences();

            if(surfaceHolder != null)
            {
                dragGame.surfaceDestroyed(surfaceHolder);
            }
        }

        KillThreads();

        super.onPause();
    }

    private void Initialise()
    {
        puzzle = getIntent().getParcelableExtra("puzzle");

        imageArray = new ArrayList<>();
        length = 0;

        ResetPreferencesValues();

        textView = (TextView)findViewById(R.id.score);
        resetButton = (Button)this.findViewById(R.id.resetButton);

        moveThreads = new ArrayList<>();

        GoToTasks(new puzzle_object[] { puzzle });

        relativeLayout = (RelativeLayout) findViewById(R.id.game);

        GoToTasks(new puzzle_object[] { });
    }

    private void ResetPreferencesValues()
    {
        score = 0;
        attempts = 0;
        correctAttempts = 0;
        squares = new ArrayList<>();
        layout = new ArrayList<>();
        highlightedSquare = new two_dimensional_vector_object(-1, -1);
        moveSquare = new drag_square_object(new two_dimensional_vector_object(-1, -1), new two_dimensional_vector_object(-1, -1));
        currentMatches = 0;
        firstBoolean = false;
    }

    public void StartButton()
    {
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ResetPreferences();

                ResetPreferencesValues();

                recreate();
            }
        });
    }

    private void ResetPreferences()
    {
        String puzzleId = puzzle.GetId();

        preferences.RemoveKey(this, puzzleId + "Drag" + "score");
        preferences.RemoveKey(this, puzzleId + "Drag" + "attempts");
        preferences.RemoveKey(this, puzzleId + "Drag" + "correctAttempts");

        for(Integer i = 0; i < squares.size(); i++)
        {
            for(Integer j = 0; j < squares.get(i).size(); j++)
            {
                preferences.RemoveKey(this, puzzleId + "Drag" + "square" + String.valueOf(i) + String.valueOf(j));
                preferences.RemoveKey(this, puzzleId + "Drag" + "layout" + String.valueOf(i) + String.valueOf(j));
            }
        }

        preferences.RemoveKey(this, puzzleId + "Drag" + "highlightedSquareX");
        preferences.RemoveKey(this, puzzleId + "Drag" + "highlightedSquareY");

        preferences.RemoveKey(this, puzzleId + "Drag" + "moveSquareSquareX");
        preferences.RemoveKey(this, puzzleId + "Drag" + "moveSquareSquareY");

        preferences.RemoveKey(this, puzzleId + "Drag" + "firstBoolean");
        preferences.RemoveKey(this, puzzleId + "Drag" + "currentMatches");
    }

    public void GoToTasks(puzzle_object[] puzzle)
    {
        new drag_game_tasks(this, imageArray, length).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, puzzle);
    }

    private void GetPreferences()
    {
        String puzzleId = puzzle.GetId();

        score = preferences.ReadInteger(this, puzzleId + "Drag" + "score", score);
        attempts = preferences.ReadInteger(this, puzzleId + "Drag" + "attempts", attempts);
        correctAttempts = preferences.ReadInteger(this, puzzleId + "Drag" + "correctAttempts", correctAttempts);

        highlightedSquare.SetX(preferences.ReadInteger(this, puzzleId + "Drag" + "highlightedSquareX", highlightedSquare.GetX()));
        highlightedSquare.SetY(preferences.ReadInteger(this, puzzleId + "Drag" + "highlightedSquareY", highlightedSquare.GetY()));

        moveSquare.GetSquare().SetX(preferences.ReadInteger(this, puzzleId + "Drag" + "moveSquareSquareX", moveSquare.GetSquare().GetX()));
        moveSquare.GetSquare().SetY(preferences.ReadInteger(this, puzzleId + "Drag" + "moveSquareSquareY", moveSquare.GetSquare().GetY()));

        moveSquare.GetPosition().SetX(preferences.ReadInteger(this, puzzleId + "Drag" + "moveSquarePositionX", moveSquare.GetPosition().GetX()));
        moveSquare.GetPosition().SetY(preferences.ReadInteger(this, puzzleId + "Drag" + "moveSquarePositionY", moveSquare.GetPosition().GetY()));

        firstBoolean = preferences.ReadBoolean(this, puzzleId + "Drag" + "firstBoolean", firstBoolean);
        currentMatches = preferences.ReadInteger(this, puzzleId + "Drag" + "currentMatches", currentMatches);
    }

    public void SetScoreTextView()
    {
        String scoreText = String.valueOf(score);
        textView.setText(scoreText);
    }

    private void SavePreferences()
    {
        String puzzleId = puzzle.GetId();

        preferences.WriteInteger(this, puzzleId + "Drag" + "score", score);
        preferences.WriteInteger(this, puzzleId + "Drag" + "attempts", attempts);
        preferences.WriteInteger(this, puzzleId + "Drag" + "correctAttempts", correctAttempts);

        for(Integer i = 0; i < squares.size(); i++)
        {
            for(Integer j = 0; j < squares.get(i).size(); j++)
            {
                preferences.WriteInteger(this, puzzleId + "Drag" + "square" + String.valueOf(i) + String.valueOf(j), squares.get(i).get(j).GetImageState());
                preferences.WriteInteger(this, puzzleId + "Drag" + "layout" + String.valueOf(i) + String.valueOf(j), squares.get(i).get(j).GetImagePosition());
            }
        }

        preferences.WriteInteger(this, puzzleId + "Drag" + "highlightedSquareX", highlightedSquare.GetX());
        preferences.WriteInteger(this, puzzleId + "Drag" + "highlightedSquareY", highlightedSquare.GetY());

        preferences.WriteInteger(this, puzzleId + "Drag" + "moveSquareSquareX", moveSquare.GetSquare().GetX());
        preferences.WriteInteger(this, puzzleId + "Drag" + "moveSquareSquareY", moveSquare.GetSquare().GetY());

        preferences.WriteBoolean(this, puzzleId + "Drag" + "firstBoolean", firstBoolean);
        preferences.WriteInteger(this, puzzleId + "Drag" + "currentMatches", currentMatches);
    }

    public void KillThreads()
    {
        if(initialiseThread != null)
        {
            initialiseThread.interrupt();
        }

        if(drawThread != null)
        {
            drawThread.interrupt();
        }

        if(updateThread != null)
        {
            updateThread.interrupt();
        }

        for(Integer i = 0; i < moveThreads.size(); i++)
        {
            Thread currentThread = moveThreads.get(i);

            if (currentThread != null)
            {
                currentThread.interrupt();
            }
        }
    }

    public void OnGameFinished()
    {
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("position", getIntent().getIntExtra("position", -1));

        ResetPreferencesValues();

        setResult(RESULT_OK, intent);

        finish();
    }
}
