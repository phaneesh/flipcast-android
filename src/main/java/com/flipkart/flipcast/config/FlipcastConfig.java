package com.flipkart.flipcast.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phaneesh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Builder
public class FlipcastConfig {

    private String host;

    private int port;

    private String endpoint;

    private boolean secured;

    private int connectionTimeout = 5000;

    private int requestTimeout = 5000;
}
