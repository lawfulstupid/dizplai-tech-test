FROM debian

# Set up application files
WORKDIR /app
ADD . /app

# Install dependencies
RUN apt update
RUN apt install -y openjdk-17-jdk maven npm curl
RUN npm install -g n    # install n for node version switching
RUN apt remove -y npm   # get rid of wrong npm version
RUN apt autoremove -y
RUN n 18                # installs node 18 and reinstalls correct npm version

# Build frontend
RUN npm install -g @angular/cli
RUN npm install --production
RUN ng build

# Build backend (with bundled frontend)
RUN mvn clean package -DskipTests
RUN cp /app/target/dizplai-tech-test-*.jar /app/target/app.jar

# Run application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/app.jar"]
