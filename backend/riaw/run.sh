cp /app/$CONFIG_FILE /app/application.properties
java -Xmx128m -Xss256k -XX:+UseSerialGC -XX:MaxRAM=72m -jar riaw.jar --spring.profiles.default=$PROFILE -Dspring.profiles.active=$PROFILE --spring.profiles.active=$PROFILE
