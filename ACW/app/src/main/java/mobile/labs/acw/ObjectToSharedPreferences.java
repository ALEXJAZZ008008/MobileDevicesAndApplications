package mobile.labs.acw;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectToSharedPreferences
{
    private MenuActivity menuActivity;

    public ObjectToSharedPreferences(Context context)
    {
        menuActivity = (MenuActivity)context;
    }

    public void ToSharedPreferences(String PREFERENCES, Object object, String key)
    {
        SharedPreferences.Editor editor = menuActivity.getSharedPreferences(PREFERENCES, menuActivity.MODE_PRIVATE).edit();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream;

        try
        {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(object);

            byte[] data = byteArrayOutputStream.toByteArray();

            objectOutputStream.close();
            byteArrayOutputStream.close();

            byteArrayOutputStream = new ByteArrayOutputStream();
            Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream, Base64.DEFAULT);

            base64OutputStream.write(data);

            base64OutputStream.close();
            byteArrayOutputStream.close();

            editor.putString(key, new String(byteArrayOutputStream.toByteArray()));

            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Object ToObject(String PREFERENCES, String key)
    {
        Object object = null;

        try
        {
            byte[] bytes = menuActivity.getSharedPreferences(PREFERENCES, menuActivity.MODE_PRIVATE).getString(key, "{}").getBytes();

            if(bytes.length == 0)
            {
                return null;
            }

            ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
            Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
            ObjectInputStream in = new ObjectInputStream(base64InputStream);

            object = in.readObject();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return object;
    }
}
