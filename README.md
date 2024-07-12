# VWO OpenFeature Provider JAVA

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Requirements

The Java SDK supports:

* Open JDK - 8 onwards
* Oracle JDK - 8 onwards

## SDK Installation

Install dependencies using `mvn install`

Add below Maven dependency in your project.


```java
<dependency>
    <groupId>com.vwo.sdk</groupId>
    <artifactId>vwo-openfeature-provider-java</artifactId>
    <version>LATEST</version>
</dependency>
```

## Basic Usage
 ```java
 import dev.openfeature.sdk.*;
 import com.vwo.VWOProvider;
 import java.util.Map;
 OpenFeatureAPI api = OpenFeatureAPI.getInstance();
 Map<String, Object> options = new HashMap<>();
 options.put("accountId", 1234);
 options.put("sdkKey", "32-alpha-numeric-sdk-key");
 api.setProvider(new VWOProvider(vwoClient));
 EvaluationContext context = new ImmutableContext(new HashMap() {
        {
            put("userId", new Value("user1")); // userId
            put("key", new Value("variableKey")); // variable key
            put("customVariables", new Value(new ImmutableContext(new HashMap() {
                {
                    put("key", new Value("value"));
                }
            })));
        }
 });
 
Client vwoClient = api.getClient("vwo-openfeature-provider-java-provider");
apiClient.getStringValue("featureKey", "defaultValue", context)
 ```

For more appenders, refer [this](https://logback.qos.ch/manual/appenders.html).

## Authors

* [Abhishek Joshi](https://github.com/Abhi591)

## Changelog

Refer [CHANGELOG.md](https://github.com/wingify/vwo-openfeature-provider-java/blob/master/CHANGELOG.md)

## Contributing

Please go through our [contributing guidelines](https://github.com/wingify/vwo-openfeature-provider-java/blob/master/CONTRIBUTING.md)

## Code of Conduct

[Code of Conduct](https://github.com/wingify/vwo-openfeature-provider-java/blob/master/CODE_OF_CONDUCT.md)

## License

[Apache License, Version 2.0](https://github.com/wingify/vwo-openfeature-provider-java/blob/master/LICENSE)

Copyright 2024 Wingify Software Pvt. Ltd.
