FROM openjdk:21-jdk
ARG VERSION=0.0.2
ARG JAR_FILE=build/libs/OCR_Backend-${VERSION}-SNAPSHOT.jar
COPY ${JAR_FILE} backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/backend.jar"]