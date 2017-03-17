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

    public ListView downloadListView;
    public CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Initialise();

        GoToTask(new String[] { "StartList" });
        StartList();
    }

    private void Initialise()
    {
        puzzleList = new ArrayList<>();
        puzzleListBoolean = false;

        downloadListView = (ListView)findViewById(R.id.list);
    }

    public void GoToTask(String[] taskArgs)
    {
        new Tasks(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArgs);
    }

    private void StartList()
    {
        customAdapter = new CustomAdapter(this, puzzleList);

        downloadListView.setAdapter(customAdapter);

        downloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(puzzleList.get(position).GetState().equals(getResources().getString(R.string.download)))
                {
                    if (!puzzleListBoolean)
                    {
                        GoToTask(new String[]{"GetPuzzle", String.valueOf(puzzleListBoolean), String.valueOf(position)});

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
    }
}