package mobile.labs.acw;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends Activity
{
    public PuzzleObject puzzle;
    public ArrayList<ImageObject> imageArray;
    public Boolean imageArrayBoolean;

    public RelativeLayout relativeLayout;
    public Canvas canvas;

    private int score;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Initialise();
    }

    private void Initialise()
    {
        puzzle = getIntent().getParcelableExtra("puzzle");

        imageArray = new ArrayList<>();
        imageArrayBoolean = false;

        score = 0;
        textView = (TextView)findViewById(R.id.score);

        SetScoreTextView();

        GoToTasks(new PuzzleObject[] { puzzle });

        relativeLayout = (RelativeLayout) findViewById(R.id.canvas);

        GoToTasks(new PuzzleObject[] { });
    }

    private void SetScoreTextView()
    {
        String scoreText = getResources().getString(R.string.scoreTitle) + String.valueOf(score);
        textView.setText(scoreText);
    }

    public void GoToTasks(PuzzleObject[] puzzle)
    {
        new GameTasks(this, imageArray).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, puzzle);
    }
}
