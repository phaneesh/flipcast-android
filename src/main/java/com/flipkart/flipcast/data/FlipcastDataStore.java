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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Implementation of {@linkplain SQLiteOpenHelper} to cache manage database operations and expose operations.
 *
 * @author Sharath Pandeshwar
 * @since 15/05/16.
 */
public class FlipcastDataStore extends SQLiteOpenHelper implements DataStoreContract {

    private static final String TAG = "FlipcastDataStore";
    private static final String DB_NAME = "flipcast.db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase writableDatabase;


    public FlipcastDataStore(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //*********************************************************************
    // Life cycles
    //*********************************************************************

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Created Database with DB Name:" + DB_NAME + " and version:" + DB_VERSION);
        TableInAppMessages.create(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        TableInAppMessages.upgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    //*********************************************************************
    // interface implementations
    //*********************************************************************

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return getWritableDatabase().update(table, values, whereClause, whereArgs);
    }

    @Override
    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        return getWritableDatabase().insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }

    @Override
    public void startTransaction() {
        getWritableDatabase().beginTransaction();
    }

    @Override
    public void endTransaction() {
        getWritableDatabase().endTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    @Override
    public int delete(String tableName, String selectionCriteria, String[] selectionArgs) {
        return getWritableDatabase().delete(tableName, selectionCriteria, selectionArgs);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (writableDatabase == null) {
            writableDatabase = super.getWritableDatabase();
        }

        return writableDatabase;
    }

    //*********************************************************************
    // End of the class
    //*********************************************************************

}
