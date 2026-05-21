package com.hudayberdiyev.lab_project_addressbook;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddressBookContentProvider extends ContentProvider {

    private static final int ONE_CONTACT = 1;
    private static final int CONTACTS = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private AddressBookDatabaseHelper dbHelper;

    static {
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DatabaseDescription.Contact.TABLE_NAME + "/#", ONE_CONTACT);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DatabaseDescription.Contact.TABLE_NAME, CONTACTS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new AddressBookDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseDescription.Contact.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                builder.appendWhere(DatabaseDescription.Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS:
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != CONTACTS) {
            throw new UnsupportedOperationException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseDescription.Contact.TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri newUri = DatabaseDescription.Contact.buildContactUri(rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        }
        throw new android.database.SQLException("Insert failed: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) != ONE_CONTACT) {
            throw new UnsupportedOperationException("Invalid URI: " + uri);
        }

        String id = uri.getLastPathSegment();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updated = db.update(DatabaseDescription.Contact.TABLE_NAME, values,
                DatabaseDescription.Contact._ID + "=" + id, selectionArgs);

        if (updated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) != ONE_CONTACT) {
            throw new UnsupportedOperationException("Invalid URI: " + uri);
        }

        String id = uri.getLastPathSegment();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleted = db.delete(DatabaseDescription.Contact.TABLE_NAME,
                DatabaseDescription.Contact._ID + "=" + id, selectionArgs);

        if (deleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}