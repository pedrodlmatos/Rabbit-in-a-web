#Build
FROM node:13.12.0-alpine3.11 AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production
RUN ls /app/dist