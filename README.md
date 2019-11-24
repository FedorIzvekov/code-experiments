# Dynamic Enum
Reflection experiment

* Run:
```
cd <projects_directory>/dynamic-enum

gradle wrapper --gradle-version <gradle_version> --stacktrace --info

./gradlew clean build

java --add-opens=java.base/jdk.internal.reflect=ALL-UNNAMED \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
    -jar ./build/libs/dynamic-enum-1.0.0-SNAPSHOT.jar
```


* Check:

To retrieve all enums:
```
curl -v http://localhost:8080/enums
```

To add a new enum:
```
curl -v -X PUT http://localhost:8080/enums/<enum_name>
```