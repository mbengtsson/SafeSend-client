package se.teamgejm.safesend.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import se.teamgejm.safesend.database.SafeSendSqlHelper;
import se.teamgejm.safesend.entities.Message;

import java.util.ArrayList;
import java.util.List;

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

    public List<Message> getAllMessage (long userId) {
        List<Message> messages = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT m.* FROM messages m WHERE m.senderId = ? OR m.receiverId = ?", new String[]{String.valueOf(userId), String.valueOf(userId)});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        return messages;
    }


    private Message cursorToMessage (Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex("_id"));
        final long senderId = cursor.getLong(cursor.getColumnIndex("senderId"));
        final long receiverId = cursor.getLong(cursor.getColumnIndex("receiverId"));
        final String message = cursor.getString(cursor.getColumnIndex("message"));
        final long dateTime = cursor.getLong(cursor.getColumnIndex("dateTime"));


        return new Message(id, null, null, null, 0, message, dateTime, Message.STATUS_DECRYPTED);
    }
}
