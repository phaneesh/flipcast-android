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

package com.flipkart.flipcast.config;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phaneesh
 */
@Data
@NoArgsConstructor
public class FlipcastConfig {

    private String host;

    private int port = 0;

    private String endpoint;

    private boolean secured;

    private int connectionTimeout = 5000;

    private int requestTimeout = 5000;

    @Builder
    public FlipcastConfig(String host, int port, String endpoint, boolean secured, int connectionTimeout, int requestTimeout) {
        this.host = host;
        if(port == 0) {
            if(secured) {
                this.port = 443;
            } else {
                this.port = 80;
            }
        } else {
            this.port = port;
        }
        this.endpoint = endpoint;
        this.secured = secured;
        if(connectionTimeout == 0) {
            this.connectionTimeout = 5000;
        } else {
            this.connectionTimeout = connectionTimeout;
        }
        if(requestTimeout == 0) {
            this.requestTimeout = 5000;
        } else {
            this.requestTimeout = requestTimeout;
        }
    }
}
