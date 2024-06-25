FROM openjdk:21-jdk
LABEL authors="dev_cbjun"

ARG JAR_FILE=./build/libs/*-SNAPSHOT.jar
ARG PROPERTIES=./src/main/resources/application.properties
ADD ${JAR_FILE} application.jar
ADD ${PROPERTIES} /home/data/application.properties

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.config.location=/home/data/application.properties", "-Djava.security.egd=file:/dev/./urandom","-jar","application.jar"]