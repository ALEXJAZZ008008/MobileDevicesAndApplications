package mobile.labs.acw;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

public class MenuActivity extends Activity
{
    public ArrayList<Puzzle> puzzleList;
    public ArrayList<String> jsonArray;
    private ListView listView;
    private CustomAdapter customAdapter;

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
        listView = (ListView)findViewById(R.id.list);

        puzzleList = new ArrayList<>();
        jsonArray = new ArrayList<>();
    }

    private void StartList()
    {
        customAdapter = new CustomAdapter(puzzleList, this);

        listView.setAdapter(customAdapter);
    }
}