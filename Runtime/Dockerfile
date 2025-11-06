FROM openjdk:26-ea-21
# LABEL authors="Pedro Akos Costa"

WORKDIR /app

COPY target/babel-backend.jar /app/babel-backend.jar
COPY resources/log4j2.xml /app/log4j2.xml

ENTRYPOINT ["java", "-Dlog4j2.configurationFile=log4j2.xml", "-jar", "babel-backend.jar"]

EXPOSE 8080