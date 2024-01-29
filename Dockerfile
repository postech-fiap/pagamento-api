FROM gradle:jdk17 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle clean presentation:build

FROM eclipse-temurin:17-jdk-alpine
COPY --from=gradleimage /home/gradle/source/presentation/build/libs/presentation-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "presentation-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
