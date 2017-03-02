package mobile.labs.lab_5_1_piraterocks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.skull);

        final CountdownView countdownView = new CountdownView(getApplicationContext(), bitmap);
        layout.addView(countdownView);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)countdownView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int count = 4;

                while(count >= -1)
                {
                    countdownView.ChangeBitmap(count);
                    countdownView.postInvalidate();

                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e)
                    {

                    }

                    count--;
                }
            }
        }).start();
    }

    class CountdownView extends ImageView
    {
        private Bitmap m_Bitmap;

        public CountdownView(Context pContext, Bitmap pBitmap)
        {
            super(pContext);

            m_Bitmap = pBitmap;

            setImageBitmap(pBitmap);
        }

        @Override
        protected void onDraw(Canvas pCanvas)
        {
            setImageBitmap(m_Bitmap);

            super.onDraw(pCanvas);
        }

        protected void ChangeBitmap(int pFrame)
        {
            switch(pFrame)
            {
                case -1:
                    Intent intent = new Intent(getApplicationContext(), SeaActivity.class);
                    startActivity(intent);

                    break;

                case 0:
                    m_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.go);

                    break;

                case 1:
                    m_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.one);

                    break;

                case 2:
                    m_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.two);

                    break;

                case 3:
                    m_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.three);

                    break;

                case 4:
                    m_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.skull);

                    break;
            }
        }
    }
}