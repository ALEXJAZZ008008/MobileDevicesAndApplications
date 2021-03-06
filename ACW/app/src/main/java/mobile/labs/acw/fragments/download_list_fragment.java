package mobile.labs.acw.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import mobile.labs.acw.utilities.custom_adapter;
import mobile.labs.acw.menu.menu_activity;
import mobile.labs.acw.R;
import mobile.labs.acw.utilities.download_internet_check;
import mobile.labs.acw.utilities.main_internet_check;

public class download_list_fragment extends Fragment
{
    private menu_activity menuActivity;
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
        menuActivity = (menu_activity)getActivity();

        menuActivity.downloadPuzzleList = new ArrayList<>();

        menuActivity.downloadListView = (ListView)view.findViewById(R.id.downloadList);
    }

    private void StartList()
    {
        //This starts the list in the download fragment
        menuActivity.downloadCustomAdapter = new custom_adapter(menuActivity, menuActivity.downloadPuzzleList);

        menuActivity.downloadListView.setAdapter(menuActivity.downloadCustomAdapter);

        //This is the listener for a click event on the download list
        menuActivity.downloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                GoToInternetCheck(position);
            }
        });
    }

    //This checks for an internet connection
    private void GoToInternetCheck(Integer position)
    {
        try
        {
            new download_internet_check(menuActivity, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}