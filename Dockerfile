FROM openjdk:18
ADD target/log-ingestor-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]