package mobile.labs.acw;

import android.app.Activity;
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
    public ArrayList<PuzzleListItemObject> puzzleList;
    public Boolean puzzleListBoolean;

    public ListView listView;
    public CustomAdapter customAdapter;

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
                        GoToTasks(new String[]{"GetPuzzle", String.valueOf(puzzleListBoolean), String.valueOf(position)});

                        Toast.makeText(view.getContext(), "Downloading puzzle", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(view.getContext(), "Currently downloading puzzle", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    GoToGameActivity(view, position);
                }
            }

            private void GoToGameActivity(View view,int position)
            {
                Intent intent = new Intent(view.getContext(), GameActivity.class);
                intent.putExtra("puzzle", puzzleList.get(position).GetPuzzle());
                startActivity(intent);
            }
        });

        GoToTasks(new String[] { "StartList" });
    }

    public void GoToTasks(String[] taskArgs)
    {
        new MenuTasks(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }
}