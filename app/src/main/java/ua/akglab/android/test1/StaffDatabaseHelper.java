package ua.akglab.android.test1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alexandr on 3/18/16.
 */
public class StaffDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stafftable.db";
    private static final int DATABASE_VERSION = 1;

    public StaffDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        StaffTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        StaffTable.onUpgrade(database, oldVersion, newVersion);
    }
}

