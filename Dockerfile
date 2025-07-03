FROM maven:3.9.9-jdk-24 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:24-jdk
#VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/sms-0.0.1-SNAPSHOT.jar sms.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar sms.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar sms.jar
