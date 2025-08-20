FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY . /app
RUN mvn -f migration/pom.xml clean package

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/migration/target/migration-0.0.1-SNAPSHOT.jar"]