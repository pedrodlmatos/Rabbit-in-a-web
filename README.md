# Rabbit-in-a-web

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

API Documentation: http://localhost:8100/swagger-ui-custom.html

Postgres details:
 - User: `postgres`
 - Password: `postgres`
 - Database name: `riaw`


## CI/CD Virtual machine

IP Address: `35.205.223.175`

Service | Internal port | External port
--- | --- | ---
PostgreSQL | 5432 | 5432
Jenkins | 8080 | 8080
Sonatype/Nexus | 8081 | 8081
Registry | 5000 | 5000


## Runtime Virtual Machine

IP Address: `34.76.46.230`

Service | Internal port | External port
--- | ---| -
PostgreSQL | 5432 | 5432
Backend | 8000 | 8100
Frontend | 30 | 3000
Nginx | 30 | 
