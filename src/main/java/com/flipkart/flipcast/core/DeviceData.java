package com.flipkart.flipcast.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phaneesh
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@Builder
public class DeviceData {

    private String configName;

    private String deviceId;

    private String cloudMessagingId;

    private String osName = "ANDROID";

    private String osVersion;

    private String brand;

    private String model;

    private String appName;

    private String appVersion;

}
