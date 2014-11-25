package se.teamgejm.safesend.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import se.teamgejm.safesend.database.SafeSendSqlHelper;
import se.teamgejm.safesend.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emil Stjerneman
 */
public class DbUserDao {

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
            Log.d("SAVED USER", userFromDb.toString());
            return userFromDb;
        }

        ContentValues values = new ContentValues();
        values.put("userId", user.getUserId());
        values.put("email", user.getEmail());
        values.put("displayName", user.getDisplayName());
        values.put("publicKey", user.getPublicKey());

        long insertId = database.insert("users", null, values);
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE _id = ?", new String[]{String.valueOf(insertId)});
        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public User getUser (long userId) {
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE userId = ?", new String[]{String.valueOf(userId)});
        cursor.moveToFirst();
        Log.d("RESTULS", "" + cursor.getCount());
        if (cursor.getCount() == 0) {
            return null;
        }
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public List<User> getAllUsers () {
        List<User> users = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM users", new String[]{});

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
        User user = new User();
        user.setId(cursor.getLong(0));
        user.setUserId(cursor.getLong(1));
        user.setEmail(cursor.getString(2));
        user.setDisplayName(cursor.getString(3));
        user.setPublicKey(cursor.getString(4));
        return user;
    }
}
