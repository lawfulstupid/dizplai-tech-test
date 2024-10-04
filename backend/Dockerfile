FROM debian

# Set up application files
WORKDIR /app
ADD . /app

# Install dependencies
RUN apt update
RUN apt install -y openjdk-17-jdk maven

# Build JAR file
RUN mvn clean package -DskipTests
RUN cp /app/target/dizplai-tech-test-*.jar /app/target/app.jar

# Run application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/app.jar"]
