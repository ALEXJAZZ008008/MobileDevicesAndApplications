package mobile.labs.acw.utilities;

import android.widget.TextView;

// View lookup cache
public class view_holder
{
    private TextView stateTextView, titleTextView, scoreTextView;

    public void SetStateTextView(TextView newStateTextView)
    {
        stateTextView = newStateTextView;
    }

    public TextView GetStateTextView()
    {
        return stateTextView;
    }

    public void SetTitleTextView(TextView newTitleTextView)
    {
        titleTextView = newTitleTextView;
    }

    public TextView GetTitleTextView()
    {
        return titleTextView;
    }

    public void SetScoreTextView(TextView newScoreTextView)
    {
        scoreTextView = newScoreTextView;
    }

    public TextView GetScoreTextView()
    {
        return scoreTextView;
    }
}
