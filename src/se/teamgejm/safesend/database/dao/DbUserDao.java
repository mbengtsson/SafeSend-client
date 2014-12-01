package se.teamgejm.safesend.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import se.teamgejm.safesend.database.SafeSendSqlHelper;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emil Stjerneman
 */
public class DbUserDao {

    private final static String TABLE_NAME = "users";

    private SQLiteDatabase database;
    private SafeSendSqlHelper sqlHelper;

    public DbUserDao (Context context) {
        sqlHelper = new SafeSendSqlHelper(context);
    }

    public void open () throws SQLException {
        database = sqlHelper.getWritableDatabase();
    }

    public void close () {
        sqlHelper.close();
    }

    public User addUser (User user) {
        final User userFromDb = getUser(user.getUserId());

        if (userFromDb != null) {
            return userFromDb;
        }

        final String query = "INSERT INTO " + TABLE_NAME + " (userId, email, displayName, publicKey) VALUES (?,?,?,?)";
        final SQLiteStatement sqLiteStatement = database.compileStatement(query);
        sqLiteStatement.bindLong(1, user.getUserId());
        sqLiteStatement.bindString(2, user.getEmail());
        sqLiteStatement.bindString(3, user.getDisplayName());
        if (user.getPublicKey() == null) {
            sqLiteStatement.bindNull(4);
        }
        else {
            sqLiteStatement.bindString(4, user.getPublicKey());
        }

        long insertId = sqLiteStatement.executeInsert();

        Cursor cursor = database.query(TABLE_NAME, null, "_id = ?", new String[]{String.valueOf(insertId)}, null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public User getUser (long userId) {
        Cursor cursor = database.query(TABLE_NAME, null, "userId = ?", new String[]{String.valueOf(userId)}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        }
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public List<User> getUsersWithMessages () {

        Cursor cursor = database.rawQuery("SELECT u.*, COUNT(m._id) xm FROM users u INNER JOIN messages m ON (m.senderId = u.userId OR m.receiverId = u.userId) WHERE u.userId != ? GROUP BY u._id HAVING xm > 0", new String[]{String.valueOf(CurrentUser.getInstance().getUserId())});
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return new ArrayList<>();
        }

        List<User> users = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    public List<User> getAllUsers () {
        List<User> users = new ArrayList<>();

        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    private User cursorToUser (Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex("_id"));
        final long userId = cursor.getLong(cursor.getColumnIndex("userId"));
        final String email = cursor.getString(cursor.getColumnIndex("email"));
        final String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
        final String publicKey = cursor.getString(cursor.getColumnIndex("publicKey"));

        return new User(id, userId, email, displayName, publicKey);
    }
}
