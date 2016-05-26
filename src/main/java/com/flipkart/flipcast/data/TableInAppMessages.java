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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.flipkart.flipcast.core.InAppMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * @author Sharath Pandeshwar
 * @since 15/05/2016
 * Table to save 'n' InAppMessages locally
 */
public class TableInAppMessages {

    private static final String TAG = "TableInAppMessages";
    public static final String NAME = "InAppMessages";


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONFIG_NAME = "type";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DEVICE_ID = "deviceId";
    public static final String COLUMN_MESSAGE_TYPE = "messageType";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_READ_STATUS = "read_status";

    public static final String COLUMN_TTL = "ttl";
    public static final String COLUMN_CREATED = "created";

    public static void create(final SQLiteDatabase db) {

        final String columnDef = TextUtils.join(SQLConstants.COMMA, new String[]{
                String.format(Locale.US, SQLConstants.DATA_INTEGER_PK, BaseColumns._ID),
                String.format(Locale.US, SQLConstants.DATA_TEXT_UNIQUE_NOT_NULL, COLUMN_ID),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_CONFIG_NAME, ""),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_PRIORITY, ""),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_DEVICE_ID, ""),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_MESSAGE_TYPE, ""),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_MESSAGE, ""),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_TAGS, ""),
                String.format(Locale.US, SQLConstants.DATA_TEXT, COLUMN_STATUS, ""),
                String.format(Locale.US, SQLConstants.DATA_INTEGER, COLUMN_TTL, 0),
                String.format(Locale.US, SQLConstants.DATA_INTEGER, COLUMN_CREATED, 0)});

        Log.d(TAG, "Column Def:" + columnDef);
        db.execSQL(String.format(Locale.US, SQLConstants.CREATE_TABLE, NAME, columnDef));
    }

    public static void upgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.i(TAG, "On Upgrade of TableInAppMessages Called. Just dropping the table and recreating it.");
        db.execSQL("DROP TABLE IF EXISTS " + NAME);
        create(db);
    }

    public static void clear(final SQLiteDatabase db) {
        Log.i(TAG, "On Clear of TableInAppMessages Called.");
        db.execSQL("DROP TABLE IF EXISTS " + NAME);
        create(db);
    }

    //*********************************************************************
    // APIs
    //*********************************************************************

    public static ContentValues getContentValues(InAppMessage inAppMessage) {
        ContentValues values = new ContentValues();
        values.put(TableInAppMessages.COLUMN_ID, inAppMessage.getId());
        values.put(TableInAppMessages.COLUMN_CONFIG_NAME, inAppMessage.getConfigName());
        values.put(TableInAppMessages.COLUMN_PRIORITY, inAppMessage.getPriority());
        values.put(TableInAppMessages.COLUMN_DEVICE_ID, inAppMessage.getDeviceId());
        values.put(TableInAppMessages.COLUMN_MESSAGE_TYPE, inAppMessage.getMessageType());
        values.put(TableInAppMessages.COLUMN_MESSAGE, inAppMessage.getMessage());

        values.put(TableInAppMessages.COLUMN_TAGS, getCommaSeparatedTags(inAppMessage));

        values.put(TableInAppMessages.COLUMN_TTL, inAppMessage.getTtl());
        values.put(TableInAppMessages.COLUMN_STATUS, inAppMessage.getStatus());
        values.put(TableInAppMessages.COLUMN_CREATED, inAppMessage.getCreated());
        return values;
    }

    public static InAppMessage readSingleInAppMessageFromCursor(final Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_ID));
        String configName = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_CONFIG_NAME));
        String priority = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_PRIORITY));
        String deviceId = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_DEVICE_ID));
        String messageType = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_MESSAGE_TYPE));
        String message = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_MESSAGE));

        String commaSeparatedTag = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_TAGS));
        List<String> tags = getTagsFromCommaSeparatedTag(commaSeparatedTag);
        String status = cursor.getString(cursor.getColumnIndex(TableInAppMessages.COLUMN_STATUS));
        Long ttl = cursor.getLong(cursor.getColumnIndex(TableInAppMessages.COLUMN_TTL));
        Long created = cursor.getLong(cursor.getColumnIndex(TableInAppMessages.COLUMN_CREATED));

        return new InAppMessage(id, configName, priority, deviceId, messageType, message, tags, ttl, status, created);
    }

    //*********************************************************************
    // Utility methods
    //*********************************************************************

    private static String getCommaSeparatedTags(InAppMessage inAppMessage) {
        if (inAppMessage.getTags() == null || inAppMessage.getTags().size() == 0) {
            return "";
        }
        String commaSeparatedTagString = "";
        for (String tag : inAppMessage.getTags()) {
            commaSeparatedTagString += tag + ",";
        }

        return commaSeparatedTagString;
    }

    private static List<String> getTagsFromCommaSeparatedTag(String commaString) {
        if (commaString == null || commaString.length() == 0) {
            return null;
        }

        if (commaString.charAt(commaString.length() - 1) == ',') {
            commaString = commaString.substring(0, commaString.length() - 1);
        }

        return Arrays.asList(commaString.split("\\s*,\\s*"));
    }

    //*********************************************************************
    // Private classes/interfaces
    //*********************************************************************


    private interface SQLConstants {
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s);";
        public static final String DATA_TEXT = "%s TEXT DEFAULT '%s' ";
        public static final String DATA_TEXT_UNIQUE_NOT_NULL = "%s TEXT NOT NULL UNIQUE";
        public static final String DATA_INTEGER = "%s INTEGER DEFAULT %d ";
        public static final String DATA_INTEGER_PK = "%s INTEGER PRIMARY KEY AUTOINCREMENT ";
        public static final String AND = " AND ";
        public static final String OR = " OR ";
        public static final String COMMA = ",";
    }


    //*********************************************************************
    // End of class
    //*********************************************************************

}
