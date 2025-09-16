FROM eclipse-temurin:21-jre
WORKDIR /app

COPY target/app.jar app.jar

EXPOSE 9002
ENTRYPOINT ["java","-jar","app.jar"]
