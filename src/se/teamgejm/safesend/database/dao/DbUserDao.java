package se.teamgejm.safesend.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import se.teamgejm.safesend.database.UserTable;
import se.teamgejm.safesend.database.model.DbUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emil Stjerneman
 */
public class DbUserDao {

    private SQLiteDatabase database;
    private UserTable userTable;

    public DbUserDao (Context context) {
        userTable = new UserTable(context);
    }

    public void open () throws SQLException {
        database = userTable.getWritableDatabase();
    }

    public void close () {
        userTable.close();
    }

    public DbUser addUser (DbUser user) {
        ContentValues values = new ContentValues();
        values.put("userId", user.getUserId());
        values.put("email", user.getEmail());
        values.put("displayName", user.getDisplayName());
        values.put("publicKey", user.getDisplayName());

        long insertId = database.insert("users", null, values);
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE _id = ?", new String[]{String.valueOf(insertId)});
        cursor.moveToFirst();
        DbUser newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public List<DbUser> getAllUsers () {
        List<DbUser> users = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM users", new String[]{});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DbUser user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    private DbUser cursorToUser (Cursor cursor) {
        DbUser user = new DbUser();
        user.setId(cursor.getLong(0));
        user.setUserId(cursor.getLong(1));
        user.setEmail(cursor.getString(2));
        user.setDisplayName(cursor.getString(3));
        user.setPublicKey(cursor.getString(4));
        return user;
    }
}
