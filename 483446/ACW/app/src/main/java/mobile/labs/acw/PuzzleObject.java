package mobile.labs.acw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PuzzleObject implements Parcelable
{
    private  String Id, PictureSet, Rows;
    private ArrayList<String> Layout;
    private  ArrayList<Bitmap> images;

    public PuzzleObject()
    {
        Layout = new ArrayList<>();
        images = new ArrayList<>();
    }

    public PuzzleObject(Parcel parcel)
    {
        SetId(parcel.readString());
        SetPictureSet(parcel.readString());
        SetRows(parcel.readString());

        SetLayout(parcel.createStringArrayList());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(GetId());
        parcel.writeString(GetPictureSet());
        parcel.writeString(GetRows());

        parcel.writeStringList(GetLayout());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        @Override
        public PuzzleObject createFromParcel(Parcel parcel)
        {
            return new PuzzleObject(parcel);
        }

        @Override
        public PuzzleObject[] newArray(int size)
        {
            return new PuzzleObject[size];
        }
    };

    public void SetId(String newId)
    {
        Id = newId;
    }

    public String GetId()
    {
        return Id;
    }

    public void SetPictureSet(String newPictureSet)
    {
        PictureSet = newPictureSet;
    }

    public String GetPictureSet()
    {
        return PictureSet;
    }

    public void SetRows(String newRows)
    {
        Rows = newRows;
    }

    public String GetRows()
    {
        return Rows;
    }

    public void SetLayout(ArrayList<String> newLayout)
    {
        Layout = newLayout;
    }

    public ArrayList<String> GetLayout()
    {
        return Layout;
    }

    public void SetImages(ArrayList<Bitmap> newImages)
    {
        images = newImages;
    }

    public ArrayList<Bitmap> GetImages()
    {
        return images;
    }
}
