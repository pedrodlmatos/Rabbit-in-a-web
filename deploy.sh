#!/bin/bash
docker stop hiah-backend && docker rm hiah-backend || echo "Backend project not found"
docker image rm 35.195.9.62:5000/v2/hiah-backend:runtime
docker run -p 8100:8000 -e CONFIG_FILE="application-runtime.properties" -e spring_profiles_activate='runtime' -e PROFILE="runtime" -d --name=hiah-backend 35.195.9.62:5000/v2/hiah-backend:runtime

docker stop hiah-frontend && docker rm hiah-frontend || echo "Frontend project not found"
docker image rm 35.195.9.62:5000/v2/hiah-frontend:runtime
docker run -p 50080:80 -d --name=hiah-frontend 35.195.9.62:5000/v2/hiah-frontend:runtime