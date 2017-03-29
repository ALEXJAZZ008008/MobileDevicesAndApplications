package mobile.labs.acw.click_game;

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
import mobile.labs.acw.utilities.preferences;
import mobile.labs.acw.objects.puzzle_object;
import mobile.labs.acw.R;
import mobile.labs.acw.objects.square_object;
import mobile.labs.acw.objects.two_dimensional_vector_object;

public class click_game_activity extends Activity
{
    public puzzle_object puzzle;

    public ArrayList<image_object> imageArray;
    public Integer length;

    public click_game clickGame;
    public RelativeLayout relativeLayout;

    public SurfaceHolder surfaceHolder;

    public Integer score, attempts, correctAttempts;
    private TextView textView;

    public ArrayList<ArrayList<square_object>> squares;
    public two_dimensional_vector_object highlightedSquare;
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
        SavePreferences();

        clickGame.surfaceDestroyed(surfaceHolder);

        super.onPause();
    }

    private void Initialise()
    {
        puzzle = getIntent().getParcelableExtra("puzzle");

        imageArray = new ArrayList<>();
        length = 0;

        ResetPreferencesValues();

        StartButton();

        textView = (TextView)findViewById(R.id.score);

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
        highlightedSquare = new two_dimensional_vector_object(-1, -1);
        currentMatches = 0;
        firstBoolean = false;
    }

    private void StartButton()
    {
        Button resetButton = (Button)this.findViewById(R.id.resetButton);

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

        preferences.RemoveKey(this, puzzleId + "Click" + "scoreInteger");
        preferences.RemoveKey(this, puzzleId + "Click" + "attempts");
        preferences.RemoveKey(this, puzzleId + "Click" + "correctAttempts");

        for(Integer i = 0; i < squares.size(); i++)
        {
            for(Integer j = 0; j < squares.get(i).size(); j++)
            {
                preferences.RemoveKey(this, puzzleId + "Click" + "square" + String.valueOf(i) + String.valueOf(j));
            }
        }

        preferences.RemoveKey(this, puzzleId + "Click" + "highlightedSquareX");
        preferences.RemoveKey(this, puzzleId + "Click" + "highlightedSquareY");

        preferences.RemoveKey(this, puzzleId + "Click" + "firstBoolean");
        preferences.RemoveKey(this, puzzleId + "Click" + "currentMatches");
    }

    public void GoToTasks(puzzle_object[] puzzle)
    {
        new click_game_tasks(this, imageArray, length).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, puzzle);
    }

    private void GetPreferences()
    {
        String puzzleId = puzzle.GetId();

        score = preferences.ReadInteger(this, puzzleId + "Click" + "scoreInteger", score);
        attempts = preferences.ReadInteger(this, puzzleId + "Click" + "attempts", attempts);
        correctAttempts = preferences.ReadInteger(this, puzzleId + "Click" + "correctAttempts", correctAttempts);

        highlightedSquare.SetX(preferences.ReadInteger(this, puzzleId + "Click" + "highlightedSquareX", highlightedSquare.GetX()));
        highlightedSquare.SetY(preferences.ReadInteger(this, puzzleId + "Click" + "highlightedSquareY", highlightedSquare.GetY()));

        firstBoolean = preferences.ReadBoolean(this, puzzleId + "Click" + "firstBoolean", firstBoolean);
        currentMatches = preferences.ReadInteger(this, puzzleId + "Click" + "currentMatches", currentMatches);
    }

    public void SetScoreTextView()
    {
        String scoreText = String.valueOf(score);
        textView.setText(scoreText);
    }

    private void SavePreferences()
    {
        String puzzleId = puzzle.GetId();

        preferences.WriteInteger(this, puzzleId + "Click" + "scoreInteger", score);
        preferences.WriteInteger(this, puzzleId + "Click" + "attempts", attempts);
        preferences.WriteInteger(this, puzzleId + "Click" + "correctAttempts", correctAttempts);

        for(Integer i = 0; i < squares.size(); i++)
        {
            for(Integer j = 0; j < squares.get(i).size(); j++)
            {
                preferences.WriteInteger(this, puzzleId + "Click" + "square" + String.valueOf(i) + String.valueOf(j), squares.get(i).get(j).GetImageState());
            }
        }

        preferences.WriteInteger(this, puzzleId + "Click" + "highlightedSquareX", highlightedSquare.GetX());
        preferences.WriteInteger(this, puzzleId + "Click" + "highlightedSquareY", highlightedSquare.GetY());

        preferences.WriteBoolean(this, puzzleId + "Click" + "firstBoolean", firstBoolean);
        preferences.WriteInteger(this, puzzleId + "Click" + "currentMatches", currentMatches);
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
