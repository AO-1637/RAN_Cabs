package com.android.ran.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.ran.data.UserContract.UserEntry;

/**
 * Created by rishab on 14/3/18.
 */

public class UserDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = UserDBHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "users.db";
    public static final int DATABASE_VERSION = 1;

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_USERS_TABLE = "CREATE TABLE " +
                UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_CONTACT + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_PASSWORD + " TEXT NOT NULL);" ;
        db.execSQL(SQL_CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ UserEntry.TABLE_NAME);
        onCreate(db);

    }
}

