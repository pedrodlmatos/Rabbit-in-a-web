#Build
FROM node:13.12.0-alpine3.11 AS build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY ./package.json /app/
COPY ./yarn.lock /app/
RUN yarn --silent
COPY . /app
RUN yarn build