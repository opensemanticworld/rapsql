# rapsql #
Containerized service stack 'Restful Age PostgreSQL' (rapsql) as ready to use development environment.

!!! Do not use this unsafe configuration it in production !!!

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
3. 

## Docker Commands on CLI
Open a terminal, navigate to your rapsql path and use docker compose tool to run the stack 

Run the stack 
~~~
    docker compose up
~~~    
This command will build and download all missing images from the docker registry. The building step may take a while, once you downloaded all images to your local machine, the command will executed way faster.
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

## References
[PostgREST](https://github.com/PostgREST/postgrest)

[Apache AGE](https://github.com/apache/age/)

[Postgres](https://github.com/postgres/postgres)


         