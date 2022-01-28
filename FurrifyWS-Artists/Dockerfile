FROM alpine:3.15.0

RUN apk update && apk add --no-cache openjdk17-jdk

COPY artists-monolith/target/furrifyws-storage-artists.jar furrifyws-storage-artists.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-artists.jar"]
