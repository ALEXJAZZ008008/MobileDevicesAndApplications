package mobile.labs.acw;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class GameChoiceFragment extends DialogFragment
{
    private MenuActivity menuActivity;

    private View view;

    private static View outView;
    private static Integer outPosition;

    private Button clickButton, dragButton;

    public GameChoiceFragment()
    {

    }

    public static GameChoiceFragment newInstance(String title, View inView, Integer inPosition)
    {
        outView = inView;
        outPosition = inPosition;

        GameChoiceFragment gameChoiceFragment = new GameChoiceFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        gameChoiceFragment.setArguments(args);
        return gameChoiceFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_game_choice, container);

        Initialise();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", menuActivity.getString(R.string.Choose_Game_Type));
        getDialog().setTitle(title);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void Initialise()
    {
        menuActivity = (MenuActivity)getActivity();

        clickButton = (Button)view.findViewById(R.id.clickButton);
        dragButton = (Button)view.findViewById(R.id.dragButton);

        clickButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menuActivity.GoToClickGameActivity(outView, outPosition);

                dismiss();
            }
        });

        dragButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
    }
}
