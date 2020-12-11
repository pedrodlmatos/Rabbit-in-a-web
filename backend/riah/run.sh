ls
cp /app/$CONFIG_FILE /app/application.properties
java -Xmx32m -Xss256k -XX:+UseSerialGC -XX:MaxRAM=72m -jar es.jar --spring.profiles.default=$PROFILE -Dspring.profiles.active=$PROFILE --spring.profiles.active=$PROFILE