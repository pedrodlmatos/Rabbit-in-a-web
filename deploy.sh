#!/bin/bash
docker stop riaw-backend && docker rm riaw-backend || echo "Backend project not found"
docker image rm 35.205.223.175:5000/v2/riaw-backend:runtime
docker run -p 8100:8000 -e CONFIG_FILE="application-runtime.properties" -e spring_profiles_activate='runtime' -e PROFILE="runtime" -d --name=riaw-backend 35.205.223.175:5000/v2/riaw-backend:runtime
docker start riaw-backend

docker stop riaw-frontend && docker rm riaw-frontend || echo "Frontend project not found"
docker image rm 35.205.223.175:5000/v2/riaw-frontend:runtime
docker run -p 3000:80 -d --name=riaw-frontend 35.205.223.175:5000/v2/riaw-frontend:runtime