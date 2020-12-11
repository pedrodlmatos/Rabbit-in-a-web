#BUILD
FROM node:13.12.0-alpine3.11 AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production
RUN ls /app/dist
#RUN
#FROM nginx:1.17.9-alpine
#COPY --from=build /app/dist/dashboard-project /usr/share/nginx/html
#COPY default.conf /etc/nginx/conf.d/
#COPY domain.key /etc/nginx/
#COPY domain.crt /etc/nginx/