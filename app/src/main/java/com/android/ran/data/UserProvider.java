package com.android.ran.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.ran.data.UserContract.UserEntry;

/**
 * Created by rishab on 14/3/18.
 */


public class UserProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the users table
     */
    private static final int USERS = 100;

    /**
     * URI matcher code for the content URI for a single user in the users table
     */
    private static final int USER_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH_USERS, USERS);
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH_USERS + "/#", USER_ID);
    }

    private UserDBHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = UserProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new UserDBHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case USERS:
                cursor = database.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set the notification URI on the cursor
        //so wwe know what content uri cursor was created for
        //if the data for the uri changes then we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return insertUser(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertUser(Uri uri, ContentValues values) {

        String email = values.getAsString(UserEntry.COLUMN_USER_EMAIL);
        String contactNumber = values.getAsString(UserEntry.COLUMN_USER_CONTACT);
        String password = values.getAsString(UserEntry.COLUMN_USER_PASSWORD);
        String userName=values.getAsString(UserEntry.COLUMN_USER_NAME);

        if (TextUtils.isEmpty(email) ||TextUtils.isEmpty(contactNumber)||TextUtils.isEmpty(password)||TextUtils.isEmpty(userName)) {
            throw new IllegalArgumentException("Invalid data");
        }
        if (contactNumber == null) {
            throw new IllegalArgumentException("Invalid contact number");
        }
        if (password==null) {
            throw new IllegalArgumentException("Invalid password");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(UserEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //notify all the listeners  that the data has changed for the pet content uri
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return updateUser(uri, contentValues, selection, selectionArgs);
            case USER_ID:
                // For the USER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateUser(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    /**
     * Update users in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateUser(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(UserEntry.COLUMN_USER_EMAIL)) {
            String email = values.getAsString(UserEntry.COLUMN_USER_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("email required");
            }
        }

        if (values.containsKey(UserEntry.COLUMN_USER_NAME)) {
            String userName = values.getAsString(UserEntry.COLUMN_USER_NAME);
            if (userName == null) {
                throw new IllegalArgumentException("user name required");
            }
        }

        if (values.containsKey(UserEntry.COLUMN_USER_CONTACT)) {
            String contact = values.getAsString(UserEntry.COLUMN_USER_CONTACT);
            if (contact == null) {
                throw new IllegalArgumentException("contact required");
            }
        }

        if (values.containsKey(UserEntry.COLUMN_USER_PASSWORD)) {
            String password = values.getAsString(UserEntry.COLUMN_USER_PASSWORD);
            if (password == null) {
                throw new IllegalArgumentException("password required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);//to notify that the uri has been changed
        }
        return rowsUpdated;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ID:
                // Delete a single row given by the ID in the URI
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return UserEntry.CONTENT_LIST_TYPE;
            case USER_ID:
                return UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

