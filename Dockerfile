FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /HarmonyLink

COPY pom.xml .
COPY src ./src
COPY src/main/resources/harmonykeystore.p12 harmonykeystore.p12

ARG HARMONY_DB_LOGIN
ARG HARMONY_DB_PASSWORD
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_SECRET
ARG HARMONYLINK_EMAIL
ARG HARMONYLINK_EMAIL_PASSWORD

ENV HARMONY_DB_LOGIN=${HARMONY_DB_LOGIN}
ENV HARMONY_DB_PASSWORD=${HARMONY_DB_PASSWORD}
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
ENV HARMONYLINK_EMAIL=${HARMONYLINK_EMAIL}
ENV HARMONYLINK_EMAIL_PASSWORD=${HARMONYLINK_EMAIL_PASSWORD}


RUN mvn clean install

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /HarmonyLink/target/HarmonyLink-0.0.1-SNAPSHOT.jar ./harmonylink.jar

COPY .env .env
COPY --from=build /HarmonyLink/src/main/resources ./resources
COPY --from=build /HarmonyLink/logs ./logs

EXPOSE 8443
CMD ["java", "-jar", "harmonylink.jar"]