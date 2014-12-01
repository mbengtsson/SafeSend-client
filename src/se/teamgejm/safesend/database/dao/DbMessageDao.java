package se.teamgejm.safesend.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import se.teamgejm.safesend.database.SafeSendSqlHelper;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emil Stjerneman
 */
public class DbMessageDao {

    private final static String TABLE_NAME = "messages";

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
        Log.d("DbMessageDao", "addMessage");

        final String query = "INSERT INTO " + TABLE_NAME + " (senderId, receiverId, message, timestamp) VALUES (?,?,?,?)";
        final SQLiteStatement sqLiteStatement = database.compileStatement(query);
        sqLiteStatement.bindLong(1, message.getSender().getUserId());
        sqLiteStatement.bindLong(2, message.getReceiver().getUserId());
        sqLiteStatement.bindString(3, message.getMessage());
        sqLiteStatement.bindLong(4, message.getTimeStamp());
        long insertId = sqLiteStatement.executeInsert();

        Cursor cursor = database.query(TABLE_NAME, new String[]{"_id as mid", "*"}, "_id = ?", new String[]{String.valueOf(insertId)}, null, null, null);
        cursor.moveToFirst();
        Message newMessage = cursorToMessage(cursor);
        cursor.close();
        return newMessage;
    }

    public void deleteConversationWithUser (Long userId) {
        Log.d("DbMessageDao", "deleteConversationWithUser");

        database.delete(TABLE_NAME, "receiverId = ?" + " AND senderId = ?",
                new String[]{String.valueOf(CurrentUser.getInstance().getUserId()), String.valueOf(userId)});
        database.delete(TABLE_NAME, "senderId = ?" + " AND receiverId = ?",
                new String[]{String.valueOf(CurrentUser.getInstance().getUserId()), String.valueOf(userId)});
    }

    public List<Message> getAllMessage (long userId) {
        Log.d("DbMessageDao", "getAllMessage");
        List<Message> messages = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT m._id as mid, m.*, s._id sid, s.userId suserId, s.email semail, s.displayName sdisplayName, s.publicKey spublicKey FROM messages m INNER JOIN users s ON m.senderId = s.userId WHERE m.senderId = ? OR m.receiverId = ? ORDER BY m.timestamp DESC", new String[]{String.valueOf(userId), String.valueOf(userId)});
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
