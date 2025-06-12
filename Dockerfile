FROM openjdk:17-slim
WORKDIR /app
COPY build/libs/*.jar walletapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "walletapp.jar"]