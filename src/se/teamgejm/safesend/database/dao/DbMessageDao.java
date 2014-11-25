package se.teamgejm.safesend.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import se.teamgejm.safesend.database.SafeSendSqlHelper;
import se.teamgejm.safesend.entities.Message;

/**
 * @author Emil Stjerneman
 */
public class DbMessageDao {

    private SQLiteDatabase database;
    private SafeSendSqlHelper sqlHelper;

    public DbMessageDao (Context context) {
        sqlHelper = new SafeSendSqlHelper(context);
    }

    public void open () throws SQLException {
        database = sqlHelper.getWritableDatabase();
    }

    public void close () {
        sqlHelper.close();
    }

    public Message addMessage (Message message) {
        ContentValues values = new ContentValues();
        if (message.getSender() != null) {
            values.put("senderId", message.getSender().getUserId());
        }
        values.put("receiverId", message.getReceiver().getUserId());
        values.put("message", message.getMessage());
        values.put("dateTime", message.getTimeStamp());

        long insertId = database.insert("messages", null, values);
        Cursor cursor = database.rawQuery("SELECT * FROM messages WHERE _id = ?", new String[]{String.valueOf(insertId)});
        cursor.moveToFirst();
        Message newMessage = cursorToMessage(cursor);
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
    private Message cursorToMessage (Cursor cursor) {
        Message message = new Message();
        message.setId(cursor.getLong(0));

        message.setMessage(cursor.getString(3));
        message.setTimeStamp(cursor.getLong(4));

        return message;
    }
}
