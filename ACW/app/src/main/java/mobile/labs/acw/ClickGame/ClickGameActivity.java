package mobile.labs.acw.ClickGame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import mobile.labs.acw.Objects.ImageObject;
import mobile.labs.acw.Utilities.Preferences;
import mobile.labs.acw.Objects.PuzzleObject;
import mobile.labs.acw.R;
import mobile.labs.acw.Objects.SquareObject;
import mobile.labs.acw.Objects.TwoDimensionalVectorObject;

public class ClickGameActivity extends Activity
{
    public PuzzleObject puzzle;

    public ArrayList<ImageObject> imageArray;
    public Integer length;

    public ClickGame clickGame;
    public RelativeLayout relativeLayout;

    public SurfaceHolder surfaceHolder;

    public Integer score, attempts, correctAttempts;
    private TextView textView;

    public ArrayList<ArrayList<SquareObject>> squares;
    public TwoDimensionalVectorObject highlightedSquare;
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

        textView = (TextView)findViewById(R.id.score);

        GoToTasks(new PuzzleObject[] { puzzle });

        relativeLayout = (RelativeLayout) findViewById(R.id.game);

        GoToTasks(new PuzzleObject[] { });
    }

    public void GoToTasks(PuzzleObject[] puzzle)
    {
        new ClickGameTasks(this, imageArray, length).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, puzzle);
    }

    private void ResetPreferencesValues()
    {
        score = 0;
        attempts = 0;
        correctAttempts = 0;
        squares = new ArrayList<>();
        highlightedSquare = new TwoDimensionalVectorObject(-1, -1);
        currentMatches = 0;
        firstBoolean = false;
    }

    private void GetPreferences()
    {
        score = Preferences.ReadInteger(this, "score", score);
        attempts = Preferences.ReadInteger(this, "attempts", attempts);
        correctAttempts = Preferences.ReadInteger(this, "correctAttempts", correctAttempts);

        highlightedSquare.SetX(Preferences.ReadInteger(this, "highlightedSquareX", highlightedSquare.GetX()));
        highlightedSquare.SetY(Preferences.ReadInteger(this, "highlightedSquareY", highlightedSquare.GetY()));

        firstBoolean = Preferences.ReadBoolean(this, "firstBoolean", firstBoolean);
        currentMatches = Preferences.ReadInteger(this, "currentMatches", currentMatches);
    }

    public void SetScoreTextView()
    {
        String scoreText = String.valueOf(score);
        textView.setText(scoreText);
    }

    private void SavePreferences()
    {
        Preferences.WriteInteger(this, "score", score);
        Preferences.WriteInteger(this, "attempts", attempts);
        Preferences.WriteInteger(this, "correctAttempts", correctAttempts);

        for(Integer i = 0; i < squares.size(); i++)
        {
            for(Integer j = 0; j < squares.get(i).size(); j++)
            {
                Preferences.WriteInteger(this, "square" + String.valueOf(i) + String.valueOf(j), squares.get(i).get(j).GetImageState());
            }
        }

        Preferences.WriteInteger(this, "highlightedSquareX", highlightedSquare.GetX());
        Preferences.WriteInteger(this, "highlightedSquareY", highlightedSquare.GetY());

        Preferences.WriteBoolean(this, "firstBoolean", firstBoolean);
        Preferences.WriteInteger(this, "currentMatches", currentMatches);
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
