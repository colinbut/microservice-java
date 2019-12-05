# Microservice - Java

This is an example skeleton of a java microservice. Primarily used for demonstration as part of the wider microservice-aws-demo project of mine.
This project can be served as a template for creating a new java 8 spring boot microservice.

## Technologies

- Java 8
- Spring Boot
- Maven
- Docker

## Building

The project is a maven-based project (using the Maven build automation tool) so to build the project can execute:

```bash
mvn clean install
```

or can use the supplied _maven wrapper_ provided within this project (if don't have maven installed on the system):

```bash
./mvnw clean install
```

## Running locally

can run directly using java:

```bash
java -jar target/microservice-java-[VERSION].jar
```

where VERSION is the version defined inside the `pom.xml`

or can run inside a Docker container. (A `Dockerfile` is provided as part of this project too)

__build__

```bash
docker build -t microservice-java .
```

__run__

e.g.

```bash
docker run -d --rm -p 8080:8080 microservice-java 
```

