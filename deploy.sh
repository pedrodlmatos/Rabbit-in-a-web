#!/bin/bash
docker stop hiah-backend && docker rm hiah-backend || echo "Backend project not found"
docker image rm 35.205.94.208:5000/v2/hiah-backend:runtime
docker run -p 8100:8000 -e CONFIG_FILE="application-runtime.properties" -e spring_profiles_activate='runtime' -e PROFILE="runtime" -d --name=hiah-backend 35.205.94.208:5000/v2/hiah-backend:runtime
docker start hiah-backend

docker stop hiah-frontend && docker rm hiah-frontend || echo "Frontend project not found"
docker image rm 35.205.94.208:5000/v2/hiah-frontend:runtime
docker run -p 3000:80 -d --name=hiah-frontend 35.205.94.208:5000/v2/hiah-frontend:runtime