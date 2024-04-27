FROM maven:3.8.3-openjdk-17 AS build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
COPY --from=build target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
