package labs.mobile.lab_6_piratecrew;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PirateDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pirates.db";

    public PirateDBHelper(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase pDb){
        pDb.execSQL(PirateDBContract.SQL_CREATE_PIRATE_TABLE);
        pDb.execSQL(PirateDBContract.SQL_CREATE_SHIP_TABLE);
        pDb.execSQL(PirateDBContract.SQL_CREATE_CREW_TABLE);
    }

    public void onUpgrade(SQLiteDatabase pDb, int pOldVersion, int pNewVerison) {
        /*
        * This method is required because it is an abstract method in SQLiteOpenHelper
        * We should be using it to upgrade the database from an older version to a newer version
        * */
    }
}
