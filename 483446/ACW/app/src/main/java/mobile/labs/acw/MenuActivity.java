package mobile.labs.acw;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MenuActivity extends Activity
{
    private ArrayList<PuzzleListItemObject> puzzleList;
    public Boolean puzzleListBoolean;

    private ListView listView;
    public CustomAdapter customAdapter;

    static final int GAME_ACTIVITY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Initialise();

        StartList();
    }

    private void Initialise()
    {
        puzzleList = new ArrayList<>();
        puzzleListBoolean = false;

        listView = (ListView)findViewById(R.id.list);
    }

    private void StartList()
    {
        customAdapter = new CustomAdapter(this, puzzleList);

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(puzzleList.get(position).GetState().equals(getResources().getString(R.string.download)))
                {
                    if (!puzzleListBoolean)
                    {
                        puzzleListBoolean = true;

                        GoToTasks(new String[]{"GetPuzzle", String.valueOf(position)});

                        Toast.makeText(view.getContext(), "Downloading puzzle", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(view.getContext(), "Currently downloading puzzle", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    ShowGameTypeChoiceDialog(view, position);
                }
            }

            private void ShowGameTypeChoiceDialog(View view, Integer position)
            {
                FragmentManager fragmentManager = getFragmentManager();
                GameTypeChoice editNameDialogFragment = GameTypeChoice.newInstance("Choose Game Type", view, position);
                editNameDialogFragment.show(fragmentManager, "dialog_fragment_game_type_choice");
            }

            private void GoToClickGameActivity(View view, Integer position)
            {
                Intent intent = new Intent(view.getContext(), ClickGameActivity.class);
                intent.putExtra("puzzle", puzzleList.get(position).GetPuzzle());
                intent.putExtra("position", position);
                startActivityForResult(intent, GAME_ACTIVITY_REQUEST);
            }
        });

        GoToTasks(new String[] { "StartList" });
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == GAME_ACTIVITY_REQUEST)
        {
            if(data.hasExtra("score") && data.hasExtra("position"))
            {
                Integer score = data.getIntExtra("score", -1);
                Integer position = data.getIntExtra("position", -1);

                PuzzleListItemObject puzzleListItem = puzzleList.get(position);

                if(Integer.valueOf(puzzleListItem.GetScore()) < score)
                {
                    puzzleListItem.SetScore(String.valueOf(score));
                }

                puzzleList.set(position, puzzleListItem);

                customAdapter.notifyDataSetChanged();
            }
        }
    }

    private void GoToTasks(String[] taskArgs)
    {
        new MenuTasks(this, puzzleList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }
}