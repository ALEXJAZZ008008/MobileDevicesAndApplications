package mobile.labs.acw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GoToMenuActivity();
    }

    private void GoToMenuActivity()
    {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

        finish();
    }
}