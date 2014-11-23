package se.teamgejm.safesend.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import se.teamgejm.safesend.database.MessageTable;
import se.teamgejm.safesend.database.model.DbMessage;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author Emil Stjerneman
 */
public class DbMessageDao {

    private SQLiteDatabase database;
    private MessageTable messageTable;

    public DbMessageDao (Context context) {
        messageTable = new MessageTable(context);
    }

    public void open () throws SQLException {
        database = messageTable.getWritableDatabase();
    }

    public void close () {
        messageTable.close();
    }

    public DbMessage addMessage (DbMessage message) {
        ContentValues values = new ContentValues();
        values.put("message", message.getMessage());
        values.put("status", "FAKE");
        values.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));

        long insertId = database.insert("messages", null, values);
        Cursor cursor = database.rawQuery("SELECT * FROM messages WHERE _id = ?", new String[]{String.valueOf(insertId)});
        cursor.moveToFirst();
        DbMessage newMessage = cursorToMessage(cursor);
        cursor.close();
        return newMessage;
    }

    //    public List<Comment> getAllComments () {
    //        List<Comment> comments = new ArrayList<Comment>();
    //
    //        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
    //                allColumns, null, null, null, null, null);
    //
    //        cursor.moveToFirst();
    //        while (!cursor.isAfterLast()) {
    //            Comment comment = cursorToComment(cursor);
    //            comments.add(comment);
    //            cursor.moveToNext();
    //        }
    //        // make sure to close the cursor
    //        cursor.close();
    //        return comments;
    //    }
    //
    private DbMessage cursorToMessage (Cursor cursor) {
        DbMessage message = new DbMessage();
        message.setId(cursor.getLong(0));
        message.setMessage(cursor.getString(1));
        return message;
    }
}
