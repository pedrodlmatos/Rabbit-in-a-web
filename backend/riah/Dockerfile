FROM adoptopenjdk/openjdk8-openj9:alpine-slim
WORKDIR /app
COPY target/riah-0.0.1-SNAPSHOT.jar riah.jar
COPY run.sh /app
COPY src/main/resources /app
RUN ls /app
ENV CONFIG_FILE=application.properties
ENV APP=riah_rest
ENV PROFILE=runtime
ENTRYPOINT ["sh", "run.sh"]