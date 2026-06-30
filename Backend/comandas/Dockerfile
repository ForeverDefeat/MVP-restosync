FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN addgroup --system restosync && adduser --system --ingroup restosync restosync

COPY --from=build /app/target/*.jar app.jar

USER restosync
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
