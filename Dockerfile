FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /HarmonyLink
COPY pom.xml .
COPY src ./src
RUN mvn clean install

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /HarmonyLink/target/HarmonyLink-0.0.1-SNAPSHOT.jar ./harmonylink.jar
EXPOSE 8080
CMD ["java", "-jar", "harmonylink.jar"]