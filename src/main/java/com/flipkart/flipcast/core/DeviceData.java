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

package com.flipkart.flipcast.core;

import android.os.Build;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Data;

/**
 * @author phaneesh
 */
@Data
public class DeviceData {

    //Hide the default constructor
    private DeviceData() {

    }

    @SerializedName("configName")
    private String configName;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("cloudMessagingId")
    private String cloudMessagingId;

    @SerializedName("osName")
    private String osName = "ANDROID";

    @SerializedName("osVersion")
    private String osVersion;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("appName")
    private String appName;

    @SerializedName("appVersion")
    private String appVersion;

    @Builder
    public DeviceData(String configName, String deviceId, String cloudMessagingId, String appName, String appVersion) {
        this.configName = configName;
        this.deviceId = deviceId;
        this.cloudMessagingId = cloudMessagingId;
        this.osName = "ANDROID";
        this.osVersion = Build.VERSION.RELEASE;
        this.brand = Build.BRAND;
        this.model = Build.MODEL;
        this.appName = appName;
        this.appVersion = appVersion;
    }
}
