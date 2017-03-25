package mobile.labs.acw.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import mobile.labs.acw.Utilities.CustomAdapter;
import mobile.labs.acw.Menu.MenuActivity;
import mobile.labs.acw.R;

public class DownloadListFragment extends Fragment
{
    private MenuActivity menuActivity;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_download_list, container, false);

        Initialise();

        StartList();

        return view;
    }

    private void Initialise()
    {
        menuActivity = (MenuActivity)getActivity();

        menuActivity.downloadPuzzleList = new ArrayList<>();

        menuActivity.downloadListView = (ListView)view.findViewById(R.id.downloadList);
    }

    private void StartList()
    {
        menuActivity.downloadCustomAdapter = new CustomAdapter(menuActivity, menuActivity.downloadPuzzleList);

        menuActivity.downloadListView.setAdapter(menuActivity.downloadCustomAdapter);

        menuActivity.downloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (!menuActivity.puzzleListBoolean)
                {
                    menuActivity.puzzleListBoolean = true;

                    menuActivity.GoToDownloadTasks(new String[]{"GetPuzzle", String.valueOf(position)});

                    Toast.makeText(menuActivity, "Downloading puzzle", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(menuActivity, "Currently downloading puzzle", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}