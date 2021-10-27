.PHONY: all postgres backend frontend nginx stop_all stop_postgres stop_backend stop_frontend stop_nginx

all: 
	docker-compose up -d --build

postgres: 
	docker-compose up -d --build postgres

backend: 
	docker-compose up -d --build

frontend: 
	docker-compose up -d --build frontend

nginx: 
	docker-compose up -d --build nginx

stop_all:
	docker-compose down

stop_postgres:
	docker stop postgres
	docker rm postgres

stop_backend:
	docker stop backend
	docker rm backend

stop_frontend:
	docker stop frontend
	docker rm frontend

stop_nginx:
	docker stop nginx
	docker rm nginx
