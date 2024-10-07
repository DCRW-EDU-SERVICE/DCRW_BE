# base image
FROM openjdk:17-jdk-alpine

# copy jar file
COPY ./build/libs/DCRW_BE-0.0.1-SNAPSHOT.jar /app.jar

# run the app
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]