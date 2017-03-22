package mobile.labs.acw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

public class MenuActivity extends Activity
{
    private ObjectToSharedPreferences objectToSharedPreferences;

    private ArrayList<PuzzleListItemObject> puzzleList;
    public ArrayList<PuzzleListItemObject> downloadPuzzleList, playPuzzleList;
    public Boolean puzzleListBoolean;

    public ListView downloadListView, playListView;
    public CustomAdapter downloadCustomAdapter, playCustomAdapter;

    private static final String PREFERENCES = "menuPreferences";
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

        ArrayList<PuzzleListItemObject> temporaryPuzzleList = (ArrayList<PuzzleListItemObject>)objectToSharedPreferences.ToObject(PREFERENCES, "puzzleList");

        if(temporaryPuzzleList != null)
        {
            puzzleList = temporaryPuzzleList;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        objectToSharedPreferences.ToSharedPreferences(PREFERENCES, puzzleList, "puzzleList");
    }

    private void Initialise()
    {
        objectToSharedPreferences = new ObjectToSharedPreferences(this);

        puzzleList = new ArrayList<>();

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

            if(puzzleListItem.GetState().equals(getResources().getString(R.string.download)))
            {
                downloadPuzzleList.add(puzzleListItem);
            }
            else
            {
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
                }

                playPuzzleList.set(position, puzzleListItem);

                playCustomAdapter.notifyDataSetChanged();
            }
        }
    }
}