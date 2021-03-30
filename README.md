# Hare in a Hat - Web App

Rabbit in a Hat desenvolvida num ambiente _web_ com UI desenvolvida em ReactJS e uma camada de serviços desenvolvida em SpringBoot.

## Backend

API desenvolvida em SpringBoot para fazer a gestão das sessões de ETL e guardar a informação relativa a bases de dados, tabelas, campos numa base de dados relacional.

## Frontend

UI desenvolvida em React com as funcionalidades já desenvolvidas na aplicação _desktop_ [Rabbit in a Hat](http://ohdsi.github.io/WhiteRabbit/RabbitInAHat.html).

## Instruções

Para correr a aplicação pode executar o seguinte comando docker 

`docker-compose up -d --build` 

## Docker services and ports

Service | Internal port | External port
--- | ---| ---
postgres | 5432 | 5432
backend | 8000 | 8100
frontend | 3000 | 3000

Postgres details:
 - User: `postgres`
 - Password: `postgres`
 - Database name: `hiah`


## CI/CD Virtual machine

IP Address: `http://35.195.116.140/`

Service | Docker container | Internal port | External port
--- | ---| --- | ---
PostgreSQL | yes | 5432 | 5432
Jenkins | no | - | 8080
Sonatype/Nexus | yes | 8081 | 8081
Registry | yes | 5000 | 5000


## Runtime Virtual Machine

IP Address: `104.199.21.27`

Service | Internal port | External port
--- | ---| --- 
PostgreSQL | 5432 | 5432
Backend | 8000 | 8100
Frontend | 30 | 3000
Nginx | 30 | 