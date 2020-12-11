FROM maven:3.5.2-jdk-8-alpine AS build
COPY . /tmp/
WORKDIR /tmp/
RUN mvn clean package -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk8-openj9:alpine-slim
WORKDIR /app
COPY --from=0 /tmp/target/riah-0.0.1-SNAPSHOT riah.jar
COPY src/main/resources /app
COPY run.sh /app
RUN ls /app
ENV CONFIG_FILE=application.properties
ENV APP=riah
ENV PROFILE=runtime
ENTRYPOINT ["sh", "run.sh"]