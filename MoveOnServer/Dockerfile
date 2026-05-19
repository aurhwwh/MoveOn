FROM maven:3.9-eclipse-temurin-17

WORKDIR /app
COPY . .

RUN mvn dependency:go-offline

CMD ["mvn", "compile", "exec:java"]