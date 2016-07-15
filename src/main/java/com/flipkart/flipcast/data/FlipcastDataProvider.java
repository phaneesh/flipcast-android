/*
 * Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.flipkart.flipcast.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flipkart.flipcast.core.InAppMessage;


/**
 * Provider that gives access to cached {@linkplain InAppMessage} through standard {@link ContentProvider} mechanism.
 * Note: This being a library project, this content provider cannot be registered independently in manifest.
 * <p></p>
 * So it is the work of Application to call the constructor with right authority. Use {@link FlipcastUriGenerator}
 * to form URIs for the supported operations.
 *
 * @author Sharath Pandeshwar
 * @since 15/05/16.
 */
public class FlipcastDataProvider extends ContentProvider {

    public static final String TAG = "FlipcastDataProvider";
    public static final String SCHEME = "content";
    public final String DEFAULT_AUTHORITY;
    public static final String PATH = "inAppMessages";
    public static final String PATH_ID = "id";
    public static final String QUERY_PARAM_ID = "id";

    final String URL;
    public final Uri CONTENT_URI;
    private final UriMatcher mUriMatcher;
    private FlipcastDataStore mFlipcastDataStore;
    private Context mContext;
    private CacheUtils mCacheUtils;

    private static final int CODE_ALL_MESSAGES = 1000;
    private static final int CODE_SINGLE_MESSAGE = 1001;

    public FlipcastDataProvider(@NonNull Context context, String authority) {
        DEFAULT_AUTHORITY = authority;
        mContext = context;
        URL = SCHEME + "://" + DEFAULT_AUTHORITY + "/" + PATH;
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mFlipcastDataStore = new FlipcastDataStore(context);
        mCacheUtils = new CacheUtils(context);
        CONTENT_URI = Uri.parse(URL);
        mUriMatcher.addURI(DEFAULT_AUTHORITY, PATH, CODE_ALL_MESSAGES);
        mUriMatcher.addURI(DEFAULT_AUTHORITY, PATH + "/" + PATH_ID, CODE_SINGLE_MESSAGE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    public static String getPath() {
        return PATH;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case CODE_ALL_MESSAGES:
                return "vnd.android.cursor.dir/vnd.flipcast.inAppMessage";

            case CODE_SINGLE_MESSAGE:
                return "vnd.android.cursor.item/vnd.flipcast.inAppMessage";

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (mUriMatcher.match(uri)) {
            case CODE_ALL_MESSAGES:
                return fetchAllInAppMessages();

            case CODE_SINGLE_MESSAGE:
                return fetchSingleInAppMessage(uri);
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case CODE_ALL_MESSAGES:
                return insertInAppMessage(uri, contentValues);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        switch (mUriMatcher.match(uri)) {
            case CODE_ALL_MESSAGES:
                return clearAllInAppMessages();

            case CODE_SINGLE_MESSAGE:
                return deleteSingleInAppMessage(uri);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        return handleUpdateOperation(uri, contentValues, where, whereArgs);
    }

    //*********************************************************************
    // Utility methods
    //*********************************************************************

    /**
     * Returns cursor pointing to all cached {@linkplain InAppMessage} objects.
     *
     * @return Cursor
     */
    private Cursor fetchAllInAppMessages() {
        String orderBy = TableInAppMessages.COLUMN_CREATED + " DESC";
        Cursor cursor = mFlipcastDataStore.query(TableInAppMessages.NAME, null, null, null, null, null, orderBy);
        printInAppMessage(cursor);
        return cursor;
    }

    /**
     * Returns cursor pointing to cached {@linkplain InAppMessage} pointed by URI with 'id' query parameter.
     *
     * @param uri Uri carrying 'id' of the row
     * @return Cursor
     */
    private Cursor fetchSingleInAppMessage(@NonNull Uri uri) {
        String id = uri.getQueryParameter(QUERY_PARAM_ID);
        String table = TableInAppMessages.NAME;
        String where = TableInAppMessages.COLUMN_ID + "=?";
        String[] args = new String[]{id};

        return mFlipcastDataStore.query(table, null, where, args, null, null, null);
    }


    /**
     * Insert an {@linkplain InAppMessage} into the cache
     *
     * @param uri           Uri pointing all InAppMessages
     * @param contentValues CV holding necessary values
     * @return Uri carrying inserted row information
     */
    private Uri insertInAppMessage(Uri uri, ContentValues contentValues) {
        long rowId = mFlipcastDataStore.insertWithOnConflict(TableInAppMessages.NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (mCacheUtils.getInAppCacheLimit() != -1) {
            trimTableToRows(mCacheUtils.getInAppCacheLimit());
        }

        if (mContext != null) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        //Log.i(TAG, "Inserted at row with id=" + rowId);
        return uri.buildUpon().appendPath(String.valueOf(rowId)).build();
    }


    /**
     * Helper to clear all cached {@linkplain InAppMessage}
     *
     * @return delete count
     */
    private int clearAllInAppMessages() {
        int deleteCount = mFlipcastDataStore.delete(TableInAppMessages.NAME, null, null);
        Log.i(TAG, "Deleted " + deleteCount + " rows");
        return deleteCount;
    }


    /**
     * Prints list of {@linkplain InAppMessage} objects pointed by the cursor
     *
     * @param tempCursor cursor
     */
    private void printInAppMessage(Cursor tempCursor) {
        if (tempCursor != null) {
            tempCursor.moveToFirst();
            while (!tempCursor.isAfterLast()) {
                InAppMessage message = TableInAppMessages.readSingleInAppMessageFromCursor(tempCursor);
                Log.i(TAG, message.toString());
                tempCursor.moveToNext();
            }
        }
    }


    /**
     * Helper to trim the row count to specified input
     *
     * @param count Number of rows that the table to be trimmed
     */
    private void trimTableToRows(int count) {
        String table = TableInAppMessages.NAME;
        String innerSelect = "select " + TableInAppMessages.COLUMN_ID + " from " + table + " order by " + TableInAppMessages.COLUMN_CREATED + " desc limit ?";
        String where = TableInAppMessages.COLUMN_ID + " not in ( " + innerSelect + " )";
        String[] args = new String[]{Integer.toString(count)};
        int deleteCount = mFlipcastDataStore.delete(TableInAppMessages.NAME, where, args);
        Log.i(TAG, "Trimmed the table. Deleted " + deleteCount + " rows.");
    }


    /**
     * Helper method to handle updating an InAppMessage data.
     *
     * @param uri
     * @param contentValues
     * @param where
     * @param whereArgs
     * @return
     */
    private int handleUpdateOperation(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        int updateResult = mFlipcastDataStore.update(TableInAppMessages.NAME, contentValues, where, whereArgs);
        Log.i(TAG, "Update result: " + updateResult);
        return updateResult;
    }


    /**
     * Handles deleting a single inApp message
     *
     * @param uri Uri carrying 'id' of the row
     * @return Cursor
     */
    private int deleteSingleInAppMessage(@NonNull Uri uri) {
        String id = uri.getQueryParameter(QUERY_PARAM_ID);
        String table = TableInAppMessages.NAME;
        String where = TableInAppMessages.COLUMN_ID + "=?";
        String[] args = new String[]{id};

        return mFlipcastDataStore.delete(table, where, args);
    }

    //*********************************************************************
    // End of the class
    //*********************************************************************
}
