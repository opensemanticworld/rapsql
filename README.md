# rapsql #
!!! For DEV purposes only, do not use in production with insecure credentials and privileges !!!

RAPSQL is a pseudonym for 'Restful Age PostgreSQL'. On the one hand, it serves to provide a ready-to-use stack to a public community for the further development of open source database services. On the other hand, features like rest-compatible queries for Apache AGE Cypher queries are implemented (currently in progress). Other features that are being developed as part of a feasibility study on the interoperability of graph databases using SPARQL with Apache AGE will also be added to this repo in the future.

## How to get started ##
You need to have [docker with compose](https://docs.docker.com/get-docker/) installed on your machine. There are just a few steps to get this rapsql-stack running. Just follow this short instrucitons (=

1. Clone this repository 
2. Configuration
    1. Change file name 'dotenv' to '.env'
    2. (optional) Open .env and change your credentials
        1. Note that there are some dependencies to the initial building script in ./pgdbconf/schema.sql
        2. You can find comment tags where changes have no or different dependencies
    3. Since there is no official docker image for apache/age-viewer at this time, you need to build this one one your own. And If you are not familiar with creating your own local docker images or don't want to use age-viewer, you can simply open docker-compose.yml and comment out this service. But don't worry, every necessary step is described below ;)
        1. clone repo https://github.com/apache/age-viewer to a local folder
        2. open docker-compose.yml 
            1. Navigate to age-viewer
            2. Change context "/usr/local/docker/age-viewer/" to your own path
            3. And that's it :)

## Docker Commands on CLI
Once all the pre-settings are complete, we can get the Rapsql stack up and running. Simply open a terminal or shell, navigate to your Rapsql path and use the docker compose tool.

Run the stack 
~~~
    docker compose up
~~~    
This command builds and downloads all missing images from the Docker registry. The build step can take a while. Once you have downloaded all the images to your local machine, the command runs much faster.
(Optional) Run services in background using "detached" mode (-d flag)
~~~
    docker compose up -d
~~~  
See what is currently running
~~~
    docker compose ps
~~~  
Shut down containers
~~~
    docker compose stop
~~~     
Run the stopped containers again
~~~
    docker compose start
~~~ 
Delete all containers without docker volumes
~~~
    docker compose down
~~~ 
Delete all containers with volumes using -v flag
~~~
    docker compose down -v
~~~ 

## Features


## Services and References

[PostgREST](https://github.com/PostgREST/postgrest)

[Apache AGE](https://github.com/apache/age/)

[Postgres](https://github.com/postgres/postgres)


         