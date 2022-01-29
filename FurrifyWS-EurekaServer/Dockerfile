FROM alpine:3.15.0

RUN apk update && apk add --no-cache openjdk17-jdk

COPY target/furrifyws-storage-eureka-server.jar furrifyws-storage-eureka-server.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-eureka-server.jar"]
