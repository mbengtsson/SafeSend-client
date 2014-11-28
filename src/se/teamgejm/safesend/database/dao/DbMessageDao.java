package se.teamgejm.safesend.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import se.teamgejm.safesend.database.SafeSendSqlHelper;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;

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
        Log.e("SENDER", message.getSender().toString());
        values.put("senderId", message.getSender().getUserId());
        values.put("receiverId", message.getReceiver().getUserId());
        values.put("message", message.getMessage());
        values.put("timestamp", message.getTimeStamp());

        long insertId = database.insert("messages", null, values);
        Cursor cursor = database.rawQuery("SELECT m._id as mid, m.* FROM messages m WHERE m._id = ?", new String[]{String.valueOf(insertId)});
        cursor.moveToFirst();
        Message newMessage = cursorToMessage(cursor);
        cursor.close();
        return newMessage;
    }

    public List<Message> getAllMessage (long userId) {
        Log.d("DbMessageDao", "getAllMessage");
        List<Message> messages = new ArrayList<>();

        //Cursor cursor = database.rawQuery("SELECT m._id as mid, m.*, s._id sid, s.userId suserId, s.email semail, s.displayName sdisplayName, s.publicKey spublicKey, r._id rid, r.userId ruserId, r.email remail, r.displayName rdisplayName, r.publicKey rpublicKey FROM messages m INNER JOIN users s ON m.senderId = s.userId INNER JOIN users r ON m.receiverId = r.userId", new String[]{String.valueOf(userId), String.valueOf(userId)});
        Cursor cursor = database.rawQuery("SELECT m._id as mid, m.*, s._id sid, s.userId suserId, s.email semail, s.displayName sdisplayName, s.publicKey spublicKey FROM messages m INNER JOIN users s ON m.senderId = s.userId WHERE m.senderId = ? OR m.receiverId = ?", new String[]{String.valueOf(userId), String.valueOf(userId)});
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
        final long mid = cursor.getLong(cursor.getColumnIndex("mid"));
        final long senderId = cursor.getLong(cursor.getColumnIndex("senderId"));
        final long receiverId = cursor.getLong(cursor.getColumnIndex("receiverId"));
        final String message = cursor.getString(cursor.getColumnIndex("message"));
        final long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));

        User sender = null;

        if (cursor.getColumnIndex("sid") != -1) {
            final long sid = cursor.getLong(cursor.getColumnIndex("sid"));
            final long suserId = cursor.getLong(cursor.getColumnIndex("suserId"));
            final String semail = cursor.getString(cursor.getColumnIndex("semail"));
            final String sdisplayName = cursor.getString(cursor.getColumnIndex("sdisplayName"));
            final String spublicKey = cursor.getString(cursor.getColumnIndex("spublicKey"));
            sender = new User(sid, senderId, semail, sdisplayName, spublicKey);
        }

        return new Message(sender, null, message, mid, timestamp);
    }
}
