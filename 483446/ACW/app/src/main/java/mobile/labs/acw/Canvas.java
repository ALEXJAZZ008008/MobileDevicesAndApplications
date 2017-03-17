package mobile.labs.acw;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Canvas extends SurfaceView implements SurfaceHolder.Callback
{
    public Canvas(Context context)
    {
        super(context);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolderolder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolderolder)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolderolder)
    {

    }
}
