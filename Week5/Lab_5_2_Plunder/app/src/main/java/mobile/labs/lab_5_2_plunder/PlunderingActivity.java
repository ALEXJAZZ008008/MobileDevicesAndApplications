package mobile.labs.lab_5_2_plunder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Random;

public class PlunderingActivity extends AppCompatActivity {

    private int m_Score;
    private ViewFlipper m_ViewFlipper;
    private ImageView m_ImageView1, m_ImageView2;
    private int m_CurrentImageView;
    private int m_CurrentImageValue;
    private Random m_RNG;
    private GestureDetector m_GestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plundering);
        m_Score = 0;
        addCurrentScore(0);
        m_RNG = new Random();
        m_ViewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper);
        m_ImageView1 = (ImageView) findViewById(R.id.ImageView1);
        m_ImageView2 = (ImageView) findViewById(R.id.ImageView2);

        SetRandomImage(m_ImageView1);
        m_CurrentImageView = 1;

        m_GestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onFling(MotionEvent pEvent1, MotionEvent pEvent2, float pVelocityX, float pVelocityY)
            {
                if(pVelocityX > 10.0f)
                {
                    FlingRight();
                }
                else
                {
                    if(pVelocityX < -10.0f)
                    {
                        FlingLeft();
                    }
                }
                return true;
            }
        });
    }

    public void FlingRight()
    {
        m_ViewFlipper.setInAnimation(inFromLeftAnimation());
        m_ViewFlipper.setOutAnimation(outToRightAnimation());
        addCurrentScore(m_CurrentImageValue);

        if(m_CurrentImageView == 1)
        {
            m_CurrentImageView = 2;
            m_CurrentImageValue = SetRandomImage(m_ImageView2);
        }
        else
        {
            m_CurrentImageView = 1;
            m_CurrentImageValue = SetRandomImage(m_ImageView1);
        }

        m_ViewFlipper.showPrevious();
    }

    public void FlingLeft()
    {
        m_ViewFlipper.setInAnimation(inFromRightAnimation());
        m_ViewFlipper.setOutAnimation(outToLeftAnimation());
        addCurrentScore(m_CurrentImageValue);

        if(m_CurrentImageView == 1)
        {
            m_CurrentImageView = 2;
            m_CurrentImageValue = SetRandomImage(m_ImageView2);
        }
        else
        {
            m_CurrentImageView = 1;
            m_CurrentImageValue = SetRandomImage(m_ImageView1);
        }

        m_ViewFlipper.showPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent pEvent)
    {
        return m_GestureDetector.onTouchEvent(pEvent);
    }

    // sets a random image to the ImageView parameter and returns the score for that image
    private int SetRandomImage(ImageView pImageView)
    {
        Bitmap bitmap = null;
        switch(m_RNG.nextInt(7))
        {
            case 0:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_chest);
                pImageView.setImageBitmap(bitmap);
                return -2;
            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wreck);
                pImageView.setImageBitmap(bitmap);
                return -4;
            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.skulls);
                pImageView.setImageBitmap(bitmap);
                return -6;
            case 3:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.treasure1);
                pImageView.setImageBitmap(bitmap);
                return 2;
            case 4:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.treasure2);
                pImageView.setImageBitmap(bitmap);
                return 3;
            case 5:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.treasure3);
                pImageView.setImageBitmap(bitmap);
                return 5;
            case 6:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.treasure4);
                pImageView.setImageBitmap(bitmap);
                return 7;
        }
        return 0;
    }

    private Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new LinearInterpolator());
        return inFromRight;
    }

    private Animation outToRightAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new LinearInterpolator());
        return outtoLeft;
    }

    private Animation inFromLeftAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new LinearInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new LinearInterpolator());
        return outtoLeft;
    }

    private void addCurrentScore(int pScore)
    {
        m_Score += pScore;
        TextView textView = (TextView)findViewById(R.id.scoreTextView);
        String scoreString = getString(R.string.score_label, m_Score);
        textView.setText(scoreString);
    }

}
