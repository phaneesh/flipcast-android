# Flipcast Android Client

Allows easier integration into Flipcast service.

## Features:
* Registering a device with the service
* Unregistering a device with the service
* Fetch in app messages (by type & priority)
* Acknowledge the in app message

## Dependencies
* retrofit
* okhttp3

## Usage

Add the following repository to your build.gradle

```
repositories {
    maven {
        url  "http://jcenter.bintray.com" 
    }
}
```

Add the dependency

```
compile 'com.flipkart.flipcast:flipcast-android:1.5.2'
```

### Sample code
```java
FlipcastConfig config = FlipcastConfig.builder()
    .host("myflipcast.com")
    .port(8080)
    .endpoint("/mobile/api")
    .secured(false)
    .build();
Flipcast flipcast = Flipcast.default()
    .config(config)
    .build();

DeviceData data = DeviceData.builder()
        .configName("myconfig");
        .deviceId("my_unique_device_identifier")
        .cloudMessagingId("gcm_id")
        .osVersion("5.1.1")
        .brand("mybrand")
        .model("mymodel")
        .appName("my_cool_app")
        .appVersion("1.0")
        .build();

flipcast.register(data, null);
```

Contributors
------------
* [Pandeshwar](https://github.com/infinitec123)

LICENSE
-------

Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
