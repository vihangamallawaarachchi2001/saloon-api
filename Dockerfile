# Use an appropriate OpenJDK base image
FROM openjdk:21-jdk

# Copy the JAR file into the container
COPY target/saloon-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app will run on (optional)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
