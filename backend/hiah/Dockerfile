FROM adoptopenjdk/openjdk8-openj9:alpine-slim
WORKDIR /app
COPY target/hiah-0.0.1-SNAPSHOT.jar hiah.jar
COPY run.sh /app
COPY src/main/resources /app
RUN ls /app
ENV CONFIG_FILE=application-runtime.properties
ENV APP=hiah
ENV PROFILE=runtime
ENTRYPOINT ["sh", "run.sh"]