package se.teamgejm.safesend.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author Emil Stjerneman
 */
public class UserTable {

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE users ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "userId INT,"
            + "email VARCHAR(255),"
            + "displayName VARCHAR(255),"
            + "publicKey TEXT"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}
