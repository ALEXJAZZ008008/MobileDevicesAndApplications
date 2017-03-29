package mobile.labs.acw.objects;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class puzzle_object implements Parcelable
{
    private  String Id, PictureSet, Rows;
    private ArrayList<String> Layout;

    public puzzle_object()
    {
        Layout = new ArrayList<>();
    }

    public puzzle_object(Parcel parcel)
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
        public puzzle_object createFromParcel(Parcel parcel)
        {
            return new puzzle_object(parcel);
        }

        @Override
        public puzzle_object[] newArray(int size)
        {
            return new puzzle_object[size];
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
}
