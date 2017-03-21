package mobile.labs.acw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DragGameActivity extends Activity
{
    public PuzzleObject puzzle;

    public ArrayList<ImageObject> imageArray;
    public Integer length;

    public DragGame dragGame;
    public RelativeLayout relativeLayout;

    public SurfaceHolder surfaceHolder;

    public Integer score, attempts, correctAttempts;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Initialise();
    }

    @Override
    protected void onPause()
    {
        dragGame.surfaceDestroyed(surfaceHolder);

        super.onPause();
    }

    private void Initialise()
    {
        puzzle = getIntent().getParcelableExtra("puzzle");

        imageArray = new ArrayList<>();
        length = 0;

        score = 0;
        attempts = 0;
        correctAttempts = 0;
        textView = (TextView)findViewById(R.id.score);

        SetScoreTextView();

        GoToTasks(new PuzzleObject[] { puzzle });

        relativeLayout = (RelativeLayout) findViewById(R.id.game);

        GoToTasks(new PuzzleObject[] { });
    }

    public void SetScoreTextView()
    {
        String scoreText = String.valueOf(score);
        textView.setText(scoreText);
    }

    public void GoToTasks(PuzzleObject[] puzzle)
    {
        new DragGameTasks(this, imageArray, length).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, puzzle);
    }

    public void OnGameFinished()
    {
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("position", getIntent().getIntExtra("position", -1));
        setResult(RESULT_OK, intent);

        finish();
    }
}
