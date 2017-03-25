package mobile.labs.acw.Menu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import mobile.labs.acw.ClickGame.ClickGameActivity;
import mobile.labs.acw.Utilities.CustomAdapter;
import mobile.labs.acw.DragGame.DragGameActivity;
import mobile.labs.acw.Utilities.Preferences;
import mobile.labs.acw.Objects.PuzzleListItemObject;
import mobile.labs.acw.Objects.PuzzleObject;
import mobile.labs.acw.R;

public class MenuActivity extends Activity
{
    private ArrayList<PuzzleListItemObject> puzzleList;
    public ArrayList<PuzzleListItemObject> downloadPuzzleList, playPuzzleList;
    public Boolean listStartedBoolean, puzzleListBoolean;

    public ListView downloadListView, playListView;
    public CustomAdapter downloadCustomAdapter, playCustomAdapter;

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

        ResetGamePreferences();

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
    }

    private void StartList()
    {
        GoToTasks(new String[] { "StartList" });
    }

    private void GoToTasks(String[] taskArgs)
    {
        new MenuTasks(this, puzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    public void GoToDownloadTasks(String[] taskArgs)
    {
        new MenuTasks(this, downloadPuzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    public void SetAndUpdateLists()
    {
        ClearLists();

        for(int i = 0; i < puzzleList.size(); i++)
        {
            PuzzleListItemObject puzzleListItem = puzzleList.get(i);

            GetPreferences(puzzleListItem);

            if(puzzleListItem.GetState().equals(getResources().getString(R.string.download)))
            {
                downloadPuzzleList.add(puzzleListItem);
            }
            else
            {
                puzzleListItem.SetState(getResources().getString(R.string.play));

                try
                {
                    Integer temporary = Integer.valueOf(puzzleListItem.GetScore());
                }
                catch(Exception e)
                {
                    puzzleListItem.SetScore("0");
                }

                playPuzzleList.add(puzzleListItem);
            }
        }

        OrderPlayPuzzleList();

        NotifyChanges();
    }

    private void ClearLists()
    {
        downloadPuzzleList.clear();
        playPuzzleList.clear();
    }

    private void GetPreferences(PuzzleListItemObject puzzleListItem)
    {
        PuzzleObject puzzleListItemPuzzle = puzzleListItem.GetPuzzle();
        String puzzleListItemTitle = puzzleListItem.GetTitle();

        ArrayList<String> puzzleListPreferences = Preferences.GetAllInstancesOfKey(this, puzzleListItemTitle);

        for (Integer j = 0; j < puzzleListPreferences.size(); j++)
        {
            String key = puzzleListPreferences.get(j);

            if (key.equals(puzzleListItemTitle))
            {
                puzzleListItemPuzzle.SetId(Preferences.ReadString(this, key, puzzleListItemPuzzle.GetId()));
            }
        }

        String puzzleListItemPuzzleId = puzzleListItem.GetPuzzle().GetId();

        if(puzzleListItemPuzzleId != null)
        {
            puzzleListPreferences = Preferences.GetAllInstancesOfKey(this, puzzleListItemPuzzleId);

            for (Integer j = 0; j < puzzleListPreferences.size(); j++)
            {
                String key = puzzleListPreferences.get(j);

                if (key.equals(puzzleListItemPuzzleId + "state"))
                {
                    puzzleListItem.SetState(Preferences.ReadString(this, key, puzzleListItem.GetState()));
                }

                if (key.equals(puzzleListItemPuzzleId + "score"))
                {
                    puzzleListItem.SetScore(Preferences.ReadString(this, key, puzzleListItem.GetScore()));
                }

                if (key.equals(puzzleListItemPuzzleId + "PictureSet"))
                {
                    puzzleListItemPuzzle.SetPictureSet(Preferences.ReadString(this, key, puzzleListItemPuzzle.GetPictureSet()));
                }

                if (key.equals(puzzleListItemPuzzleId + "Rows"))
                {
                    puzzleListItemPuzzle.SetRows(Preferences.ReadString(this, key, puzzleListItemPuzzle.GetRows()));
                }
            }

            puzzleListItemPuzzle.SetLayout(GetLayout(puzzleListItemPuzzleId));
        }
    }

    private ArrayList<String> GetLayout(String puzzleListItemPuzzleId)
    {
        String keyRegularExpression = puzzleListItemPuzzleId + "Layout";
        ArrayList<String> puzzleListPreferences = Preferences.GetAllInstancesOfKey(this, keyRegularExpression);

        Integer length = puzzleListPreferences.size();

        String[] puzzleListItemPuzzleLayout = new String[length];

        for (Integer j = 0; j < length; j++)
        {
            String key = puzzleListPreferences.get(j);

            puzzleListItemPuzzleLayout[Integer.valueOf(key.split(keyRegularExpression)[1])] = Preferences.ReadString(this, key, "-1");
        }

        return new ArrayList<>(Arrays.asList(puzzleListItemPuzzleLayout));
    }

    private void OrderPlayPuzzleList()
    {
        if(playPuzzleList.size() > 1)
        {
            ArrayList<PuzzleListItemObject> temporaryPlayPuzzleList = new ArrayList<>();
            temporaryPlayPuzzleList.addAll(playPuzzleList);

            playPuzzleList.clear();
            playPuzzleList.add(temporaryPlayPuzzleList.get(0));
            temporaryPlayPuzzleList.remove(0);

            for (Integer i = 0; i < temporaryPlayPuzzleList.size(); i++)
            {
                PuzzleListItemObject temporaryPlayPuzzleListItem = temporaryPlayPuzzleList.get(i);

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
    }

    private void NotifyChanges()
    {
        downloadCustomAdapter.notifyDataSetChanged();
        playCustomAdapter.notifyDataSetChanged();
    }

    public void GoToClickGameActivity(View view, Integer position)
    {
        Intent intent = new Intent(view.getContext(), ClickGameActivity.class);
        intent.putExtra("puzzle", playPuzzleList.get(position).GetPuzzle());
        intent.putExtra("position", position);
        startActivityForResult(intent, GAME_ACTIVITY_REQUEST);
    }

    public void GoToDragGameActivity(View view, Integer position)
    {
        Intent intent = new Intent(view.getContext(), DragGameActivity.class);
        intent.putExtra("puzzle", playPuzzleList.get(position).GetPuzzle());
        intent.putExtra("position", position);
        startActivityForResult(intent, GAME_ACTIVITY_REQUEST);
    }

    private void ResetGamePreferences()
    {
        Preferences.RemoveKey(this, "score");
        Preferences.RemoveKey(this, "attempts");
        Preferences.RemoveKey(this, "correctAttempts");

        ArrayList<String> squarePreferences = Preferences.GetAllInstancesOfKey(this, "square");

        for(Integer i = 0; i < squarePreferences.size(); i++)
        {
            Preferences.RemoveKey(this, squarePreferences.get(i));
        }

        ArrayList<String> layoutPreferences = Preferences.GetAllInstancesOfKey(this, "layout");

        for(Integer i = 0; i < layoutPreferences.size(); i++)
        {
            Preferences.RemoveKey(this, layoutPreferences.get(i));
        }

        Preferences.RemoveKey(this, "highlightedSquareX");
        Preferences.RemoveKey(this, "highlightedSquareY");

        Preferences.RemoveKey(this, "firstBoolean");
        Preferences.RemoveKey(this, "currentMatches");
    }

    private void SavePreferences()
    {
        for(Integer i = 0; i < puzzleList.size(); i++)
        {
            PuzzleListItemObject puzzleListItem = puzzleList.get(i);
            PuzzleObject puzzleListItemPuzzle = puzzleListItem.GetPuzzle();

            if(puzzleListItemPuzzle != null)
            {
                SavePuzzleListItemPreferences(puzzleListItem, puzzleListItemPuzzle);
            }
        }
    }

    private void SavePuzzleListItemPreferences(PuzzleListItemObject puzzleListItem, PuzzleObject puzzleListItemPuzzle)
    {
        String puzzleListItemPuzzleId = puzzleListItemPuzzle.GetId();

        Preferences.WriteString(this, puzzleListItem.GetTitle(), puzzleListItemPuzzleId);

        Preferences.WriteString(this, puzzleListItemPuzzleId + "state", puzzleListItem.GetState());
        Preferences.WriteString(this, puzzleListItemPuzzleId + "score", puzzleListItem.GetScore());

        Preferences.WriteString(this, puzzleListItemPuzzleId + "PictureSet", puzzleListItemPuzzle.GetPictureSet());
        Preferences.WriteString(this, puzzleListItemPuzzleId + "Rows", puzzleListItemPuzzle.GetRows());

        ArrayList<String> puzzleListItemPuzzleLayout = puzzleListItemPuzzle.GetLayout();

        for(Integer j = 0; j < puzzleListItemPuzzleLayout.size(); j++)
        {
            Preferences.WriteString(this, puzzleListItemPuzzleId + "Layout" + String.valueOf(j), puzzleListItemPuzzleLayout.get(j));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == GAME_ACTIVITY_REQUEST)
        {
            if(data.hasExtra("score") && data.hasExtra("position"))
            {
                Integer score = data.getIntExtra("score", -1);
                Integer position = data.getIntExtra("position", -1);

                PuzzleListItemObject puzzleListItem = playPuzzleList.get(position);

                if(Integer.valueOf(puzzleListItem.GetScore()) < score)
                {
                    puzzleListItem.SetScore(String.valueOf(score));

                    Preferences.WriteString(this, puzzleListItem.GetPuzzle().GetId() + "score", puzzleListItem.GetScore());
                }

                playPuzzleList.set(position, puzzleListItem);

                playCustomAdapter.notifyDataSetChanged();
            }
        }
    }
}