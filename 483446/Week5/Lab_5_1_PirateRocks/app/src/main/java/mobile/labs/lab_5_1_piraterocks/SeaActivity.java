package mobile.labs.lab_5_1_piraterocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

public class SeaActivity extends AppCompatActivity
{
    private SeaView m_SeaView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sea);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.seaLayout);
        m_SeaView = new SeaView(this);
        relativeLayout.addView(m_SeaView);

        relativeLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return m_SeaView.onTouch(v, event);
            }
        });
    }

    private class SeaView extends SurfaceView implements SurfaceHolder.Callback
    {
        private Bitmap m_RockBitmap, m_ShipBitmap;
        private Random m_RNG;
        private Paint m_Paint;
        private SurfaceHolder m_SurfaceHolder;
        private Thread m_Thread;
        private float m_ShipX, m_ShipY;

        private float m_Rock1X, m_Rock1Y, m_Rock2X, m_Rock2Y, m_Rock3X, m_Rock3Y;

        public SeaView(Context pContext)
        {
            super(pContext);
            // Get hold of the bitmaps from compiled resources
            m_RockBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rock);
            m_ShipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship);

            // instantiate other objects we will need
            m_RNG = new Random();

            m_Paint = new Paint();
            m_SurfaceHolder = getHolder();
            m_SurfaceHolder.addCallback(this);
        }

        // returns a random Y value for a rock within the range of the screen height
        private float getRandomRockY()
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            SeaActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return (float) m_RNG.nextInt(displayMetrics.heightPixels);
        }

        // returns a random X value for a rock to the left of the screen
        private float getRandomRockX()
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            SeaActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return (float) -m_RNG.nextInt(displayMetrics.widthPixels);
        }

        private void MoveRocks()
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            SeaActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float screenWidth = displayMetrics.widthPixels;

            m_Rock1X += 10;
            if(m_Rock1X > screenWidth)
            {
                m_Rock1X = getRandomRockX();
                m_Rock1Y = getRandomRockY();
            }

            m_Rock2X += 10;
            if(m_Rock2X > screenWidth)
            {
                m_Rock2X = getRandomRockX();
                m_Rock2Y = getRandomRockY();
            }

            m_Rock3X += 10;
            if(m_Rock3X > screenWidth)
            {
                m_Rock3X = getRandomRockX();
                m_Rock3Y = getRandomRockY();
            }
        }

        public boolean onTouch(View pView, MotionEvent pEvent)
        {
            if(pEvent.getAction() == MotionEvent.ACTION_DOWN || pEvent.getAction() == MotionEvent.ACTION_MOVE)
            {
                m_ShipX = pEvent.getX() - (m_ShipBitmap.getHeight() / 2);
                m_ShipY = pEvent.getY() - (m_ShipBitmap.getWidth() / 2);

                return true;
            }

            return false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder pHolder, int pFormat, int pWidth, int pHeight)
        {

        }

        @Override
        public void surfaceCreated(SurfaceHolder pHolder)
        {
            m_Thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Canvas canvas = null;

                    while (!Thread.currentThread().isInterrupted())
                    {
                        MoveRocks();

                        canvas = m_SurfaceHolder.lockCanvas();
                        canvas.drawColor(Color.parseColor("#75a2d9"));

                        canvas.drawBitmap(m_RockBitmap, m_Rock1X, m_Rock1Y, m_Paint);
                        canvas.drawBitmap(m_RockBitmap, m_Rock2X, m_Rock2Y, m_Paint);
                        canvas.drawBitmap(m_RockBitmap, m_Rock3X, m_Rock3Y, m_Paint);
                        canvas.drawBitmap(m_ShipBitmap, m_ShipX, m_ShipY, m_Paint);

                        m_SurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            });

            m_Thread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder pHolder)
        {
            if(m_Thread != null)
            {
                m_Thread.interrupt();
            }
        }
    }
}
