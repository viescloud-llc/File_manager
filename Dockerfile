FROM openjdk:19
EXPOSE 8080
EXPOSE 81
EXPOSE 82
EXPOSE 83
ADD target/file_manager.jar file_manager.jar
ENTRYPOINT ["java", "-jar", "/file_manager.jar"]