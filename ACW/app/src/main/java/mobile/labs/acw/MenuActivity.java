package mobile.labs.acw;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

public class MenuActivity extends Activity
{
    public ArrayList<Puzzle> puzzleList;

    public ListView listView;
    public CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Initialise();

        StartTask("StartList");
        StartList();
    }

    private void Initialise()
    {
        puzzleList = new ArrayList<>();

        listView = (ListView)findViewById(R.id.list);
    }

    private void StartTask(String taskArg)
    {
        new Tasks(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, taskArg);
    }

    private void StartList()
    {
        customAdapter = new CustomAdapter(puzzleList, this);

        listView.setAdapter(customAdapter);
    }
}