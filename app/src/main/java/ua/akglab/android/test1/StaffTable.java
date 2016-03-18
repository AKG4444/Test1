package ua.akglab.android.test1;

/**
 * Created by alexandr on 3/18/16.
 */
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StaffTable {

    // Database table
    public static final String TABLE_STAFF = "staff";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRSTNAME = "first_name";
    public static final String COLUMN_SECONDNAME = "second_name";
    public static final String COLUMN_BIRTHYEAR = "birthyear";
    public static final String COLUMN_BIRTHPLACE = "birthplace";
    public static final String COLUMN_POSITION = "position";


    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_STAFF
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FIRSTNAME + " text not null, "
            + COLUMN_SECONDNAME + " text not null, "
            + COLUMN_BIRTHYEAR + " text not null, "
            + COLUMN_BIRTHPLACE + " text not null, "
            + COLUMN_POSITION + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.d(StaffTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        onCreate(database);
    }
}
