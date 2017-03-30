package mobile.labs.acw.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import mobile.labs.acw.click_game.click_game_activity;
import mobile.labs.acw.utilities.custom_adapter;
import mobile.labs.acw.drag_game.drag_game_activity;
import mobile.labs.acw.utilities.preferences;
import mobile.labs.acw.objects.puzzle_list_item_object;
import mobile.labs.acw.objects.puzzle_object;
import mobile.labs.acw.R;

public class menu_activity extends Activity
{
    private ArrayList<puzzle_list_item_object> puzzleList;
    public ArrayList<puzzle_list_item_object> downloadPuzzleList, playPuzzleList, filteredPlayPuzzleList;
    public ArrayList<String> spinnerList;
    public Boolean listStartedBoolean, puzzleListBoolean;

    public ListView downloadListView, playListView;
    public custom_adapter downloadCustomAdapter, playCustomAdapter;
    public Spinner spinner;
    public ArrayAdapter<String> arrayAdapter;
    public Integer spinnerChoice;

    public Toast[] toast;
    public Integer toastInteger;

    private static final int GAME_ACTIVITY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Initialise();

        StartList();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(listStartedBoolean)
        {
            SetAndUpdateLists();
        }
    }

    @Override
    protected void onPause()
    {
        SavePreferences();

        super.onPause();
    }

    private void Initialise()
    {
        puzzleList = new ArrayList<>();

        listStartedBoolean = false;

        puzzleListBoolean = false;

        spinnerChoice = -1;

        toast = new Toast[2];
        toastInteger = 1;

        toast[0] = Toast.makeText(this, "", Toast.LENGTH_LONG);
        toast[0].show();
        toast[0].cancel();

        toast[1] = Toast.makeText(this, "", Toast.LENGTH_LONG);
        toast[1].show();
        toast[1].cancel();
    }

    //This starts the main list
    private void StartList()
    {
        GoToTasks(new String[] { "StartList" });
    }

    private void GoToTasks(String[] taskArgs)
    {
        new menu_tasks(this, puzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    //This allows the download fragment to access tasks
    public void GoToDownloadTasks(String[] taskArgs)
    {
        new menu_tasks(this, downloadPuzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    //This sets up the containing lists and then sorts/filters them
    public void SetAndUpdateLists()
    {
        ClearLists();

        for(int i = 0; i < puzzleList.size(); i++)
        {
            puzzle_list_item_object puzzleListItem = puzzleList.get(i);

            GetPreferences(puzzleListItem);

            //This adds a list to the download list
            if(puzzleListItem.GetState().equals(getResources().getString(R.string.download)))
            {
                downloadPuzzleList.add(puzzleListItem);
            }
            else
            {
                //This adds a list to the play list
                puzzleListItem.SetState(getResources().getString(R.string.play));

                try
                {
                    Integer temporary = Integer.valueOf(puzzleListItem.GetScore());
                    System.out.println(temporary);
                }
                catch(Exception e)
                {
                    puzzleListItem.SetScore("0");
                }

                playPuzzleList.add(puzzleListItem);

                String layoutSize = String.valueOf(puzzleListItem.GetPuzzle().GetLayout().size());

                //This updates the filter spinner
                if(!spinnerList.contains(layoutSize))
                {
                    spinnerList.add(layoutSize);
                }
            }
        }

        //This orders the lists
        OrderPlayPuzzleList();
        OrderSpinnerList();

        //This ensures the lists are drawn correctly
        NotifyChanges();
    }

    //This sorts the spinner list
    private void OrderSpinnerList()
    {
        Collections.sort(spinnerList);

        spinnerList.add(0, getString(R.string.all));
    }

    //This clears all the lists
    private void ClearLists()
    {
        downloadPuzzleList.clear();

        playPuzzleList.clear();
        filteredPlayPuzzleList.clear();

        spinnerList.clear();
    }

    //This downloads all relevant information
    private void GetPreferences(puzzle_list_item_object puzzleListItem)
    {
        puzzle_object puzzleListItemPuzzle = puzzleListItem.GetPuzzle();
        String puzzleListItemTitle = puzzleListItem.GetTitle();

        ArrayList<String> puzzleListPreferences = preferences.GetAllInstancesOfKey(this, puzzleListItemTitle);

        for (Integer j = 0; j < puzzleListPreferences.size(); j++)
        {
            String key = puzzleListPreferences.get(j);

            if (key.equals(puzzleListItemTitle))
            {
                puzzleListItemPuzzle.SetId(preferences.ReadString(this, key, puzzleListItemPuzzle.GetId()));
            }
        }

        String puzzleListItemPuzzleId = puzzleListItem.GetPuzzle().GetId();

        if(puzzleListItemPuzzleId != null)
        {
            puzzleListPreferences = preferences.GetAllInstancesOfKey(this, puzzleListItemPuzzleId);

            for (Integer j = 0; j < puzzleListPreferences.size(); j++)
            {
                String key = puzzleListPreferences.get(j);

                if (key.equals(puzzleListItemPuzzleId + "state"))
                {
                    puzzleListItem.SetState(preferences.ReadString(this, key, puzzleListItem.GetState()));
                }

                if (key.equals(puzzleListItemPuzzleId + "scoreString"))
                {
                    puzzleListItem.SetScore(preferences.ReadString(this, key, puzzleListItem.GetScore()));
                }

                if (key.equals(puzzleListItemPuzzleId + "PictureSet"))
                {
                    puzzleListItemPuzzle.SetPictureSet(preferences.ReadString(this, key, puzzleListItemPuzzle.GetPictureSet()));
                }

                if (key.equals(puzzleListItemPuzzleId + "Rows"))
                {
                    puzzleListItemPuzzle.SetRows(preferences.ReadString(this, key, puzzleListItemPuzzle.GetRows()));
                }
            }

            puzzleListItemPuzzle.SetLayout(GetLayout(puzzleListItemPuzzleId));
        }
    }

    //This gets the layout of a certain puzzle
    private ArrayList<String> GetLayout(String puzzleListItemPuzzleId)
    {
        String keyRegularExpression = puzzleListItemPuzzleId + "Layout";
        ArrayList<String> puzzleListPreferences = preferences.GetAllInstancesOfKey(this, keyRegularExpression);

        Integer length = puzzleListPreferences.size();

        String[] puzzleListItemPuzzleLayout = new String[length];

        for (Integer j = 0; j < length; j++)
        {
            String key = puzzleListPreferences.get(j);

            puzzleListItemPuzzleLayout[Integer.valueOf(key.split(keyRegularExpression)[1])] = preferences.ReadString(this, key, "-1");
        }

        return new ArrayList<>(Arrays.asList(puzzleListItemPuzzleLayout));
    }

    //This sorts the playlist
    private void OrderPlayPuzzleList()
    {
        if(playPuzzleList.size() > 1)
        {
            ArrayList<puzzle_list_item_object> temporaryPlayPuzzleList = new ArrayList<>();
            temporaryPlayPuzzleList.addAll(playPuzzleList);

            playPuzzleList.clear();
            playPuzzleList.add(temporaryPlayPuzzleList.get(0));
            temporaryPlayPuzzleList.remove(0);

            for (Integer i = 0; i < temporaryPlayPuzzleList.size(); i++)
            {
                puzzle_list_item_object temporaryPlayPuzzleListItem = temporaryPlayPuzzleList.get(i);

                Integer playPuzzleListLength = playPuzzleList.size();

                for (Integer j = 0; j < playPuzzleListLength; j++)
                {
                    if (temporaryPlayPuzzleListItem.GetPuzzle().GetLayout().size() < playPuzzleList.get(j).GetPuzzle().GetLayout().size())
                    {
                        playPuzzleList.add(j, temporaryPlayPuzzleListItem);

                        break;
                    }

                    if (j.equals(playPuzzleListLength - 1))
                    {
                        playPuzzleList.add(temporaryPlayPuzzleListItem);

                        break;
                    }
                }
            }
        }

        AddPlayPuzzleListItemsToFilteredList();
    }

    //This filters the play list
    private void AddPlayPuzzleListItemsToFilteredList()
    {
        for(Integer i = 0; i < playPuzzleList.size(); i++)
        {
            puzzle_list_item_object puzzleListItem = playPuzzleList.get(i);
            String layoutSize = String.valueOf(puzzleListItem.GetPuzzle().GetLayout().size());

            if (spinnerChoice == -1 || Integer.valueOf(layoutSize).equals(spinnerChoice))
            {
                filteredPlayPuzzleList.add(puzzleListItem);
            }
        }
    }

    //This allows things to be drawn correctly
    private void NotifyChanges()
    {
        downloadCustomAdapter.notifyDataSetChanged();
        playCustomAdapter.notifyDataSetChanged();

        arrayAdapter.notifyDataSetChanged();
    }

    //This goes to the click game
    public void GoToClickGameActivity(View view, Integer position)
    {
        Intent intent = new Intent(view.getContext(), click_game_activity.class);
        intent.putExtra("puzzle", filteredPlayPuzzleList.get(position).GetPuzzle());
        intent.putExtra("position", position);
        startActivityForResult(intent, GAME_ACTIVITY_REQUEST);
    }

    //This goes to the drag game
    public void GoToDragGameActivity(View view, Integer position)
    {
        Intent intent = new Intent(view.getContext(), drag_game_activity.class);
        intent.putExtra("puzzle", filteredPlayPuzzleList.get(position).GetPuzzle());
        intent.putExtra("position", position);
        startActivityForResult(intent, GAME_ACTIVITY_REQUEST);
    }

    //This saves the relevant information
    private void SavePreferences()
    {
        for(Integer i = 0; i < puzzleList.size(); i++)
        {
            puzzle_list_item_object puzzleListItem = puzzleList.get(i);
            puzzle_object puzzleListItemPuzzle = puzzleListItem.GetPuzzle();

            if(puzzleListItemPuzzle != null)
            {
                SavePuzzleListItemPreferences(puzzleListItem, puzzleListItemPuzzle);
            }
        }
    }

    //This saves the relevant information
    private void SavePuzzleListItemPreferences(puzzle_list_item_object puzzleListItem, puzzle_object puzzleListItemPuzzle)
    {
        String puzzleListItemPuzzleId = puzzleListItemPuzzle.GetId();

        preferences.WriteString(this, puzzleListItem.GetTitle(), puzzleListItemPuzzleId);

        preferences.WriteString(this, puzzleListItemPuzzleId + "state", puzzleListItem.GetState());
        preferences.WriteString(this, puzzleListItemPuzzleId + "scoreString", puzzleListItem.GetScore());

        preferences.WriteString(this, puzzleListItemPuzzleId + "PictureSet", puzzleListItemPuzzle.GetPictureSet());
        preferences.WriteString(this, puzzleListItemPuzzleId + "Rows", puzzleListItemPuzzle.GetRows());

        ArrayList<String> puzzleListItemPuzzleLayout = puzzleListItemPuzzle.GetLayout();

        for(Integer j = 0; j < puzzleListItemPuzzleLayout.size(); j++)
        {
            preferences.WriteString(this, puzzleListItemPuzzleId + "Layout" + String.valueOf(j), puzzleListItemPuzzleLayout.get(j));
        }
    }

    //This gets the correct information back from the returning activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == GAME_ACTIVITY_REQUEST)
        {
            if(data.hasExtra("score") && data.hasExtra("position"))
            {
                Integer score = data.getIntExtra("score", -1);
                Integer position = data.getIntExtra("position", -1);

                puzzle_list_item_object puzzleListItem = playPuzzleList.get(position);

                if(Integer.valueOf(puzzleListItem.GetScore()) < score)
                {
                    puzzleListItem.SetScore(String.valueOf(score));

                    preferences.WriteString(this, puzzleListItem.GetPuzzle().GetId() + "scoreString", puzzleListItem.GetScore());
                }

                playPuzzleList.set(position, puzzleListItem);

                playCustomAdapter.notifyDataSetChanged();
            }
        }
    }
}