![furrify title](https://user-images.githubusercontent.com/33985207/133672173-2d7ff06e-6f94-4742-a201-c54a85c5839a.png)

Microservices infrastructure for content management.

Documentation: - Not released yet REST endpoints (in
development): https://documenter.getpostman.com/view/9259933/TzRUBnQm

## Setup Keycloak

To set up keycloak you can use [this](#) docker image. In panel, you can import settings from [this](#) file.
! Links are not yet available as project version 0.0.1 is not released.

## Docker

This project is now docker supported. You can build it using either `build.sh` or `build.bat`.

After that you need to edit `docker-compose.yml` for your needs. All relevant environment variables are commented in
that file.

When you are ready to go, just do `docker-compose up -d` to start all the microservices.

Requirements:

- Maven
- Docker
- Docker-compose

## Compile project yourself

! All ip's can be changed in application-{profile}.yml regarding selected profile.

Requirements:

- Need to set env variable KAFKA_CONSUMER_GROUP_ID (It is any string to make groupId unique and don't lose offset in
  kafka)
- Other env variables from prod profiles (Look for them in `application-prod.yml`.)
- Kafka
- Schema Registry
- Keycloak

You can then build the project following the docker section above.

## Tests

Whole project is covered by tests using JUnit 5. Note that running tests requires dev profile or else there will be
thrown exceptions as default profile is production.

To run those you can use Maven command: `mvn clean compile test -Dspring.profiles.active=dev`
