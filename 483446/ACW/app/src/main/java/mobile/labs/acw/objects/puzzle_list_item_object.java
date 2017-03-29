package mobile.labs.acw.objects;

import android.content.Context;

import mobile.labs.acw.R;

public class puzzle_list_item_object
{
    private String state, title, score;
    private puzzle_object puzzle;

    public puzzle_list_item_object(Context context, String inTitle)
    {
        SetState(context.getResources().getString(R.string.download));
        SetTitle(inTitle);
        SetScore(context.getResources().getString(R.string.initialScore));

        puzzle = new puzzle_object();
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

    public puzzle_object GetPuzzle()
    {
        return puzzle;
    }
}
