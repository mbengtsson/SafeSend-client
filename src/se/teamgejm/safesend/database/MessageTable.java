package se.teamgejm.safesend.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author Emil Stjerneman
 */
public class MessageTable {

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE messages ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "senderId INT,"
            + "receiverId INT,"
            + "message TEXT,"
            + "dateTime TEXT"
            + ");";


    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }
}
