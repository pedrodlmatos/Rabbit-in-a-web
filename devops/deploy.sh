#!/bin/bash
docker stop hiah-backend && docker rm hiah-backend || echo "Backend project not found"
docker image rm 35.195.9.62:5000/v2/hiah-backend:runtime
docker run -p 50180:8000 -e CONFIG_FILE="application-runtime.properties" -e spring_profiles_activate='runtime' -e PROFILE="runtime" -d --name=hiah-backend 35.195.9.62:5000/v2/hiah-backend:runtime