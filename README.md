# rapsql
Containerized service stack 'Restful Age PostgreSQL' (rapsql) as ready to use development environment.

!!! Do not use this unsafe configuration it in production !!!

## How to get started
You need to have docker with compose installed on your machine. There are just a few steps to get this rapsql-stack running. Just follow this short instrucitons (=

1. Clone this repository 
2. Configuration
    1. Change file name 'dotenv' to '.env'
    2. (optional) Open .env and change your credentials
        1. Note that there are some dependencies to the initial building script in ./pgdbconf/schema.sql
        2. You can find comment tags where changes have no or different dependencies
    3. Since there is no official docker image for apache/age-viewer, you need to build this one one your own. And If you are not familiar with creating your own local docker images or don't want to use age-viewer, you can simply open docker-compose.yml and comment out this service. But don't worry, every necessary step is described below ;)
        1. clone repo https://github.com/apache/age-viewer to a local folder
        2. open docker-compose.yml 
            1. navigat to age-viewer
            2. change context "/usr/local/docker/age-viewer/" to your own <path>
            3. that's it :)
3. Open a terminal
    1. Navigate to your rapsql <path>
    2. Use docker compose tool to start the stack
        
        docker compose up
    

## References
[PostgREST](https://github.com/PostgREST/postgrest)

[Apache AGE](https://github.com/apache/age/)

[Postgres](https://github.com/postgres/postgres)


         