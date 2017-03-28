package mobile.labs.acw.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.ArrayList;
import mobile.labs.acw.Utilities.CustomAdapter;
import mobile.labs.acw.Menu.MenuActivity;
import mobile.labs.acw.R;

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
        StartSpinner();

        return view;
    }

    private void Initialise()
    {
        menuActivity = (MenuActivity)getActivity();

        menuActivity.playPuzzleList = new ArrayList<>();
        menuActivity.filteredPlayPuzzleList = new ArrayList<>();
        menuActivity.spinnerList = new ArrayList<>();

        menuActivity.playListView = (ListView)view.findViewById(R.id.playList);

        menuActivity.spinner = (Spinner)view.findViewById(R.id.spinner);
    }

    private void StartList()
    {
        menuActivity.playCustomAdapter = new CustomAdapter(menuActivity, menuActivity.filteredPlayPuzzleList);

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

    private void StartSpinner()
    {
        menuActivity.arrayAdapter = new ArrayAdapter<>(menuActivity, R.layout.spinner_template, menuActivity.spinnerList);

        menuActivity.spinner.setAdapter((menuActivity.arrayAdapter));

        menuActivity.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                menuActivity.spinnerChoice = Integer.valueOf((String)adapterView.getItemAtPosition(position));

                menuActivity.SetAndUpdateLists();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                menuActivity.spinnerChoice = -1;

                menuActivity.SetAndUpdateLists();
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