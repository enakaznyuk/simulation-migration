FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY . /app
RUN mvn -f simulation/pom.xml clean package

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/simulation/target/simulation-0.0.1-SNAPSHOT.jar"]