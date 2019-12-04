FROM openjdk:8-alpine
RUN mkdir /app
WORKDIR /app
ADD target/microservice-java-*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
