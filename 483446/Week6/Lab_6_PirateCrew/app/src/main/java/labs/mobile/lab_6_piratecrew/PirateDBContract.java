package labs.mobile.lab_6_piratecrew;

import android.provider.BaseColumns;

public final class PirateDBContract {
    private PirateDBContract(){}

    public static abstract class PirateEntry implements BaseColumns {
        public static final String TABLE_NAME = "Pirate";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_NICKNAME = "Nickname";
        public static final String COLUMN_NAME_NATIONALITY = "Nationality";
    }

    public static abstract class ShipEntry implements BaseColumns {
        public static final String TABLE_NAME = "Ship";
        public static final String COLUMN_NAME_NAME = "Name";
    }

    public static abstract class CrewMemberEntry implements BaseColumns {
        public static final String TABLE_NAME = "Crew Member";
        public static final String COLUMN_NAME_PIRATE_ID = "Pirate ID";
        public static final String COLUMN_NAME_SHIP_ID = "Ship ID";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_PIRATE_TABLE = "CREATE TABLE " + PirateEntry.TABLE_NAME +
            " (" + PirateEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            PirateEntry.COLUMN_NAME_NAME + COMMA_SEP + PirateEntry.COLUMN_NAME_NICKNAME
            + COMMA_SEP + PirateEntry.COLUMN_NAME_NATIONALITY + " )";

    public static final String SQL_CREATE_SHIP_TABLE = "CREATE TABLE " + ShipEntry.TABLE_NAME +
            " (" + ShipEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            ShipEntry.COLUMN_NAME_NAME  + " )";

    public static final String SQL_CREATE_CREW_TABLE = "CREATE TABLE " + CrewMemberEntry.TABLE_NAME +
            " (" + CrewMemberEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            CrewMemberEntry.COLUMN_NAME_PIRATE_ID + COMMA_SEP + CrewMemberEntry.COLUMN_NAME_SHIP_ID + " )";

    public static final String SQL_DELETE_PIRATE_TABLE = "DROP TABLE IF EXISTS " + PirateEntry.TABLE_NAME;
    public static final String SQL_DELETE_SHIP_TABLE = "DROP TABLE IF EXISTS " + ShipEntry.TABLE_NAME;
    public static final String SQL_DELETE_CREW_TABLE = "DROP TABLE IF EXISTS " + CrewMemberEntry.TABLE_NAME;
}
