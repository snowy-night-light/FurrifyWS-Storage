![furrify title](https://user-images.githubusercontent.com/33985207/133672173-2d7ff06e-6f94-4742-a201-c54a85c5839a.png)

Microservices infrastructure for content management.

Documentation: - Not released yet
REST endpoints: https://documenter.getpostman.com/view/9259933/TzRUBnQm

## Compile project yourself

! All ip's can be changed in application-{profile}.yml regarding selected profile.

Requirements:
- Need to set env variable KAFKA_CONSUMER_GROUP_ID (It is any string to make groupId unique and don't lose offset in
  kafka)
- Kafka (kafka:9092)
- Schema Registry (kafka:8081)
- Keycloak (192.168.0.200:6565)
- Currently no production profile is supported you will need to use "dev" profile.

## Tests
Whole project is covered by tests using JUnit 5.
Note that running tests requires dev profile or else there will be thrown exceptions as default profile is production.

To run those you can use Maven command: `mvn clean compile test -Dspring.profiles.active=dev`
