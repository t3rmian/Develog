#!/bin/bash
sudo service docker start
docker container start postgres
docker container start mongo
docker run --env-file=env_sample -p 8080:8080 -t develog