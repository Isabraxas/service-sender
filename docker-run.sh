#!/usr/bin/env bash
#Used to build and run docker image
./docker.sh
if [ $? -eq 0 ]
then
    docker rm -f boot_demo-service
    docker run -d -it --restart always -p 8080:8080 --name boot_demo-service org.gr8conf.us/docker/boot_demo-service
fi