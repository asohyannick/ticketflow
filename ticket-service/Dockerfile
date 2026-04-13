FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY . .

WORKDIR /build/ticket-service
RUN mvn clean package -DskipTests -B \
    -Dmaven.repo.local=/root/.m2/repository

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/ticket-service/target/*.jar app.jar

USER appuser

ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]