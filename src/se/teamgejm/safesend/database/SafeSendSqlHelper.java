package se.teamgejm.safesend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Emil Stjerneman
 */
public class SafeSendSqlHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "safesend.db";
    private static final int DATABASE_VERSION = 1;

    public SafeSendSqlHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        MessageTable.onCreate(db);
        UserTable.onCreate(db);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        MessageTable.onUpgrade(db, oldVersion, newVersion);
        UserTable.onUpgrade(db, oldVersion, newVersion);
    }
}
