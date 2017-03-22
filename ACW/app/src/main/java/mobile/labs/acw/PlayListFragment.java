package mobile.labs.acw;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayListFragment extends Fragment
{
    private MenuActivity menuActivity;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_play_list, container, false);

        Initialise();

        StartList();

        return view;
    }

    private void Initialise()
    {
        menuActivity = (MenuActivity)getActivity();

        menuActivity.playPuzzleList = new ArrayList<>();

        menuActivity.playListView = (ListView)view.findViewById(R.id.playList);
    }

    private void StartList()
    {
        menuActivity.playCustomAdapter = new CustomAdapter(menuActivity, menuActivity.playPuzzleList);

        menuActivity.playListView.setAdapter(menuActivity.playCustomAdapter);

        menuActivity.playListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ShowGameTypeChoiceDialog(view, position);
            }
        });
    }

    private void ShowGameTypeChoiceDialog(View view, Integer position)
    {
        FragmentManager fragmentManager = getFragmentManager();
        GameChoiceFragment editNameDialogFragment = GameChoiceFragment.newInstance(menuActivity.getString(R.string.Choose_Game_Type), view, position);
        editNameDialogFragment.show(fragmentManager, "dialog_fragment_game_type_choice");
    }
}