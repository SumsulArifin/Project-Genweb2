FROM openjdk:17-alpine

WORKDIR /app
COPY /.mvn .mvn
COPY mvnw pom.xml ./
RUN chmod 776 mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
