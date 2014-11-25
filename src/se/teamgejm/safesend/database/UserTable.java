package se.teamgejm.safesend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Emil Stjerneman
 */
public class UserTable extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "safesend.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE users ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "userId INT,"
            + "email VARCHAR(255),"
            + "displayName VARCHAR(255),"
            + "publicKey TEXT"
            + ");";

    public UserTable (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}
