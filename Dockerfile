# 1. Use official Java 17 runtime
FROM eclipse-temurin:21-jdk-alpine


# 2. Set working directory inside container
WORKDIR /app

# 3. Copy Maven wrapper and pom.xml first (helps with caching)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# 4. Copy the source code
COPY src ./src

# 5. Build the Spring Boot JAR
RUN ./mvnw clean package -DskipTests

# 6. Expose the port your Spring Boot app uses
EXPOSE 8080

# 7. Environment variables placeholders (override in Render)
ENV PORT=8080
ENV MONGO_URI=""
ENV AI_SERVICE_URL=""
ENV FRONTEND_URL=""
ENV GOOGLE_CLIENT_ID=""
ENV GOOGLE_CLIENT_SECRET=""
ENV GOOGLE_REDIRECT_URI=""

# 8. Start the Spring Boot app
CMD ["java", "-jar", "target/potyourholes-0.0.1-SNAPSHOT.jar"]
