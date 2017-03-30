package mobile.labs.acw.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

public class preferences
{
    public static final String PREFERENCES = "preferences";
    public static final Integer MODE = Context.MODE_PRIVATE;

    //This saves a bool
    public static void WriteBoolean(Context context, String key, Boolean value)
    {
        GetEditor(context).putBoolean(key, value).apply();
    }

    //This reads a bool
    public static Boolean ReadBoolean(Context context, String key, Boolean value)
    {
        return GetPreferences(context).getBoolean(key, value);
    }

    //This saves an int
    public static void WriteInteger(Context context, String key, Integer value)
    {
        GetEditor(context).putInt(key, value).apply();
    }

    //This reads an int
    public static Integer ReadInteger(Context context, String key, Integer value)
    {
        return GetPreferences(context).getInt(key, value);
    }

    //This saves a string
    public static void WriteString(Context context, String key, String value)
    {
        GetEditor(context).putString(key, value).apply();
    }

    //This reads a string
    public static String ReadString(Context context, String key, String value)
    {
        return GetPreferences(context).getString(key, value);
    }

    //This finds every key that contains a given string
    public static ArrayList<String> GetAllInstancesOfKey(Context context, String key)
    {
        String[] preferences = GetPreferences(context).getAll().keySet().toArray(new String[GetPreferences(context).getAll().keySet().size()]);
        ArrayList<String> preferencesKeyList = new ArrayList<>();

        for(String string : preferences)
        {
            if(string.contains(key))
            {
                preferencesKeyList.add(string);
            }
        }

        return preferencesKeyList;
    }

    //This removes a key and its associated contents
    public static void RemoveKey(Context context, String key)
    {
        GetEditor(context).remove(key).apply();
    }

    //This returns an editor for shared preferences
    public static SharedPreferences.Editor GetEditor(Context context)
    {
        return GetPreferences(context).edit();
    }

    //This returns shared preferences
    public static SharedPreferences GetPreferences(Context context)
    {
        return context.getSharedPreferences(PREFERENCES, MODE);
    }
}
