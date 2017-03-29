package mobile.labs.acw.fragments;

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
import mobile.labs.acw.utilities.custom_adapter;
import mobile.labs.acw.menu.menu_activity;
import mobile.labs.acw.R;

public class play_list_fragment extends Fragment
{
    private menu_activity menuActivity;
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
        menuActivity = (menu_activity)getActivity();

        menuActivity.playPuzzleList = new ArrayList<>();
        menuActivity.filteredPlayPuzzleList = new ArrayList<>();

        menuActivity.spinnerList = new ArrayList<>();

        menuActivity.playListView = (ListView)view.findViewById(R.id.playList);

        menuActivity.spinner = (Spinner)view.findViewById(R.id.spinner);
    }

    private void StartList()
    {
        menuActivity.playCustomAdapter = new custom_adapter(menuActivity, menuActivity.filteredPlayPuzzleList);

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
                String selection = (String)adapterView.getItemAtPosition(position);

                try
                {
                    menuActivity.spinnerChoice = Integer.valueOf(selection);
                }
                catch(Exception e)
                {
                    menuActivity.spinnerChoice = -1;
                }

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
        game_choice_fragment editNameDialogFragment = game_choice_fragment.newInstance(menuActivity.getString(R.string.Choose_Game_Type), view, position);
        editNameDialogFragment.show(fragmentManager, menuActivity.getString(R.string.dialog_fragment));
    }
}