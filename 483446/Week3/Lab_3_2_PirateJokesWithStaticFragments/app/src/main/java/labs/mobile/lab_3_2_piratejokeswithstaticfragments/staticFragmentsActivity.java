package labs.mobile.lab_3_2_piratejokeswithstaticfragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class staticFragmentsActivity extends AppCompatActivity implements  setupFragment.ListSelectionListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_fragments);
    }

    @Override
    public void onListSelection(int pIndex)
    {
        punchlineFragment fragment = (punchlineFragment) getFragmentManager().findFragmentById(R.id.punchline_frag);
        fragment.showPunchlineForIndex(pIndex);
    }
}
