package mobile.labs.acw.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import mobile.labs.acw.menu.menu_activity;
import mobile.labs.acw.R;

public class game_choice_fragment extends DialogFragment
{
    private menu_activity menuActivity;
    private View view;

    private static View outView;
    private static Integer outPosition;

    public static game_choice_fragment newInstance(String title, View inView, Integer inPosition)
    {
        outView = inView;
        outPosition = inPosition;

        game_choice_fragment gameChoiceFragment = new game_choice_fragment();
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
    }

    private void Initialise()
    {
        menuActivity = (menu_activity)getActivity();

        //This sets the buttons and their events in case of a click
        Button clickButton = (Button)view.findViewById(R.id.clickButton);
        Button dragButton = (Button)view.findViewById(R.id.dragButton);

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
                menuActivity.GoToDragGameActivity(outView, outPosition);

                dismiss();
            }
        });
    }
}
