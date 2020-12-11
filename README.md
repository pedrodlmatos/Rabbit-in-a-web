# Rabbit in a Hat - Web App

Rabbit in a Hat desenvolvida num ambiente _web_ com UI desenvolvida em ReactJS e uma camada de serviços desenvolvida em SpringBoot.

## Backend

API desenvolvida em SpringBoot para fazer a gestão das sessões de ETL e guardar a informação relativa a bases de dados, tabelas, campos numa base de dados relacional.

## Frontend

UI desenvolvida em React com as funcionalidades já desenvolvidas na aplicação _desktop_ [Rabbit in a Hat](http://ohdsi.github.io/WhiteRabbit/RabbitInAHat.html).

imagem do frontend

## Arquitetura

imagem da arquitetura

## Instruções

Para correr a aplicação pode executar o seguinte comando docker 

`docker-compose up -d --build` 

ou criar de forma singular cada uma componentes abaixo especificadas.


### PostgreSQL

Credenciais:
 - User: `postgres`
 - Password: `postgres`

1. `psql -U postgres`
2. `CREATE DATABASE riah`

### Backend

1. `cd backend/riah`
2. `spring-boot:run`

### Frontend

1. `cd frontend/`
2. `npm start`

## Portos utilizados

 - 3000: frontend (React)
 - 8081: backend (SpringBoot)
 - 5432: PostgreSQL