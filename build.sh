#!/bin/bash
VERSION=0.0.1-alpine

mvn clean package -Dspring.profiles.active=dev -DskipTests

docker build -t furrifyws-storage-artists:$VERSION ./FurrifyWS-Artists
docker build -t furrifyws-storage-eureka-server:$VERSION ./FurrifyWS-EurekaServer
docker build -t furrifyws-storage-gateway:$VERSION ./FurrifyWS-Gateway
docker build -t furrifyws-storage-posts:$VERSION ./FurrifyWS-Posts
docker build -t furrifyws-storage-sources:$VERSION ./FurrifyWS-Sources
docker build -t furrifyws-storage-tags:$VERSION ./FurrifyWS-Tags

echo "Images built. You can edit and run docker-compose.yml file."