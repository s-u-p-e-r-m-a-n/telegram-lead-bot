FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml
# give the mvnw file execution permissions
RUN chmod +x mvnw
# download Maven dependencies in advance
RUN ./mvnw dependency:go-offline

COPY src/ src/
# putting together a project
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]