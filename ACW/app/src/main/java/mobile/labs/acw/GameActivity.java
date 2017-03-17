package mobile.labs.acw;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity
{
    PuzzleObject puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GetIntent();
    }

    private void GetIntent()
    {
        puzzle = getIntent().getParcelableExtra("puzzle");
    }
}
