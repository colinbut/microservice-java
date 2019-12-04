FROM openjdk:8-alpine
RUN mkdir /apps
WORKDIR /apps
ADD target/microservice-java-*.jar /apps/microservice-java-app.jar
ENTRYPOINT ["java", "-jar", "microservice-java-app.jar"]
