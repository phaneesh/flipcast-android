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

import android.net.Uri;

/**
 * Helper to generate right URIs for database operations exposed by {@link FlipcastDataProvider}.
 *
 * @author Sharath Pandeshwar
 * @since 15/05/16.
 */
public class FlipcastUriGenerator {

    private String mBaseAuthority;

    public FlipcastUriGenerator(String baseAuthority) {
        this.mBaseAuthority = baseAuthority;
    }

    private Uri getBaseUri() {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme(FlipcastDataProvider.SCHEME).authority(mBaseAuthority).appendPath(FlipcastDataProvider.PATH).build();
    }

    //*********************************************************************
    // APIs
    //*********************************************************************

    /**
     * Build URI that can point to all {@linkplain com.flipkart.flipcast.core.InAppMessage} objects in cache
     *
     * @return
     */
    public Uri generateUriForAllInAppMessages() {
        return getBaseUri();
    }


    /**
     * Build URI that can point a {@linkplain com.flipkart.flipcast.core.InAppMessage} with matching id.
     *
     * @return
     */
    public Uri generateUriForSingleInAppMessage(String id) {
        return getBaseUri().buildUpon().appendPath(FlipcastDataProvider.PATH_ID).appendQueryParameter(FlipcastDataProvider.QUERY_PARAM_ID, id).build();
    }

    //*********************************************************************
    // End of class
    //*********************************************************************
}
