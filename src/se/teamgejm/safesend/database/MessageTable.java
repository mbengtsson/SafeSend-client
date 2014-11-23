package se.teamgejm.safesend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Emil Stjerneman
 */
public class MessageTable extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "safesend.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE messages ("
            + "_id INTEGER  PRIMARY KEY AUTOINCREMENT,"
            + "messageId INT,"
            + "senderId INT,"
            + "status VARCHAR(255) NOT NULL,"
            + "message TEXT NOT NULL,"
            + "dateTime TEXT NOT NULL"
            + ");";

    public MessageTable (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
