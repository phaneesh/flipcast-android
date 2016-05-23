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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Sharath Pandeshwar
 * @since 19/05/2016
 * <p></p>
 * Class to 'memorize' certain details shared preferences
 */
public class CacheUtils {

    private static final String TAG = "CacheUtils";

    /* Reference to shared preference */
    private SharedPreferences mSharedPreferences;

    /**
     * Keys
     */
    private static String sCacheLimit = "key_cache_limit";
    private static String sPollInterval = "key_poll_interval";
    private static String sBootPersistPreference = "key_boot_persist_pref";
    private static String sPollExecutorTag = "key_poll_exec_tag";

    public CacheUtils(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    //*********************************************************************
    // APIs : Cache Related
    //*********************************************************************

    /**
     * The library maintains a local cache on {@linkplain com.flipkart.flipcast.core.InAppMessage} objects.
     * But the DB is limited to 'n' rows which can be set using this function.
     *
     * @param limit
     */
    public void setInAppCacheLimit(int limit) {
        mSharedPreferences.edit().putInt(sCacheLimit, limit).apply();
    }


    /**
     * The library maintains a local cache on {@linkplain com.flipkart.flipcast.core.InAppMessage} objects.
     * But the DB is limited to 'n' rows which can be got using this function.
     */
    public int getInAppCacheLimit() {
        return mSharedPreferences.getInt(sCacheLimit, 2);
    }

    //*********************************************************************
    // End of class
    //*********************************************************************

}
