FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
RUN apt-get update -qq && apt-get install -y -qq maven > /dev/null
RUN mvn -q -B dependency:go-offline -DskipTests || true
COPY src ./src
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/omnimail-cms-*.jar app.jar
RUN mkdir -p /app/data/blobs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
