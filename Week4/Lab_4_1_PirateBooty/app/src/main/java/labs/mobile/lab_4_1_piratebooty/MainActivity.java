package labs.mobile.lab_4_1_piratebooty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private int m_Score;
    private int m_NumberOfGrogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_Score = 0;
        m_NumberOfGrogs = 0;

        setHighScore(0);
        addCurrentScore(0);
    }

    private void setHighScore(int pScore)
    {
        TextView textView = (TextView)findViewById(R.id.highScoreLabelTextView);
        String highScoreString = getString(R.string.high_score_label, pScore);
        textView.setText(highScoreString);
    }

    private void addCurrentScore(int pScore)
    {
        m_Score += pScore;
        TextView textView = (TextView)findViewById(R.id.currentScoreLabelTextView);
        String scoreString = getString(R.string.current_score_label, m_Score);
        textView.setText(scoreString);
    }

    public void drinkGrogOnClick(View pView)
    {
        m_NumberOfGrogs++;
        String grogsString = getString(R.string.grogs_toast_label, m_NumberOfGrogs);
        Toast toast = Toast.makeText(this, grogsString, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void plunderOnClick(View pView)
    {

    }
}
