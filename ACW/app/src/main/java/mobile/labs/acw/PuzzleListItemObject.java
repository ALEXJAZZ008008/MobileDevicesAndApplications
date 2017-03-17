package mobile.labs.acw;

import android.content.Context;

public class PuzzleListItemObject
{
    private String state, title, score;
    private PuzzleObject puzzle;

    public PuzzleListItemObject(Context context, String inTitle)
    {
        SetState(context.getResources().getString(R.string.download));
        SetTitle(inTitle);
        SetScore(context.getResources().getString(R.string.initialScore));

        puzzle = new PuzzleObject();
    }

    public void SetState(String newState)
    {
        state = newState;
    }

    public String GetState()
    {
        return state;
    }

    public void SetTitle(String newTitle)
    {
        title = newTitle;
    }

    public String GetTitle()
    {
        return title;
    }

    public void SetScore(String newScore)
    {
        score = newScore;
    }

    public String GetScore()
    {
        return score;
    }

    public PuzzleObject GetPuzzle()
    {
        return puzzle;
    }
}
