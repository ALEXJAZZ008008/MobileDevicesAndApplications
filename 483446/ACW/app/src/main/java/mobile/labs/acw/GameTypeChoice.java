package mobile.labs.acw;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class GameTypeChoice extends DialogFragment
{
    private static View view;
    private static Integer position;

    public GameTypeChoice()
    {

    }

    public static GameTypeChoice newInstance(String title, View inView, Integer inPosition)
    {
        view = inView;
        position = inPosition;

        GameTypeChoice gameTypeChoice = new GameTypeChoice();
        Bundle args = new Bundle();
        args.putString("title", title);
        gameTypeChoice.setArguments(args);
        return gameTypeChoice;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_template, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Choose Game Type");
        getDialog().setTitle(title);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
