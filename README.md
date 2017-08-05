# Dynamic Enum
Reflection experiment

* Run:
```
cd <projects_directory>/dynamic-enum

./gradlew clean build

java -jar ./build/libs/dynamic-enum-1.0.0-SNAPSHOT.jar
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