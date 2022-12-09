# rapsql #
RAPSQL is an acronym for 'Restful Age PostgreSQL'. On the one hand, it serves to provide a ready-to-use stack to a public community for the further development of open source database services. On the other hand, features like REST-compatible requests for Apache AGE Cypher queries are implemented (currently in progress :construction:). Other features that are being developed as part of a feasibility study on the interoperability of graph databases using SPARQL with Apache AGE will also be added to this repo in the future. :hourglass:

<br>
<p align="center">
     <img src="https://github.com/OpenSemanticLab/docker-compose-tests/blob/main/test-rapsql/src/img/rapsql-stack1.png" width="80%" height="80%">
</p>
<br>

:exclamation::exclamation::exclamation: For DEV purposes only, do not use in production with insecure credentials and privileges :exclamation::exclamation::exclamation:

## How to get started ##
First things :one:st...  you need to have [docker with compose](https://docs.docker.com/get-docker/) :whale: installed on your machine. 

You already have docker installed? Good news, there are just a few steps to get this rapsql-stack running. Just follow this short instrucitons :point_down:

1. Clone this repository to a local folder
2. Configuration
    1. Change file name 'dotenv' to '.env'
    2. (Optional) Open .env and change your credentials
        1. Note that there are some dependencies to the initial building script in ./pgdbconf/schema.sql
        2. You can find comment tags where changes have no or different dependencies
    3. Since there is no official docker image for apache/age-viewer at this time, you need to build this one your own. And If you are not familiar with creating your own local docker images or don't want to use age-viewer, you can simply open docker-compose.yml and comment out this service. But don't worry, every necessary step is described below :relaxed:
        1. Clone repo https://github.com/apache/age-viewer to another local folder
        2. Open docker-compose.yml of rapsql
        3. Navigate to service: age-viewer 
        4. Change context "/usr/local/docker/age-viewer/" to your choosen local folder path of age-viewer
    4. (ARM architecture only) E. g. on Raspberry Pi, you need to change the swagger image as well
        1. Open docker-compose.yml of rapsql
        2. Navigate to service: swagger
        3. Change the image (see comments) or comment out the hole swagger section, if you don't want to use this unofficial image
3. And that's it, we're up to deploy the stack (see next section) :sunglasses:


## Docker Commands on CLI ##
Once all the pre-settings are complete, we can get the rapsql stack up and running. Simply open a terminal or your favorite shell, navigate to your rapsql path and use the docker compose tool :muscle:

Run the stack 
~~~
    docker compose up
~~~    
This command builds and downloads all missing images from the Docker registry. The initial building step can take a few minutes. Once you have downloaded all the images to your local machine, the command runs in a few seconds. The deployment of pgdb will run [./pgdbconf/schema.sql](https://github.com/OpenSemanticLab/rapsql/blob/main/pgdbconf/schema.sql) initially and doing an open setup for Apache AGE and PostgREST. It will also create a graph called 'countries' and imports vertex and edge data from [.csv files](https://github.com/apache/age/tree/master/regress/age_load/data) that are deliverd by Apache AGE as well. :fireworks::clap:

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

To interact with postgres in CLI, you can enter your db by using a connection string with your credentials like this:
~~~
docker exec -it pgdb-container psql "postgres://<POSTGRES_USER>:<POSTGRES_PASSWORD>@<DOCKER_SERVICE_PGDB>:<POSTGRES_PORT>/<POSTGRES_DB>"
~~~ 

If you have not made any changes to your credentials after changing the file name from 'dotenv' to '.env', you can just copy the command below:
~~~
docker exec -it pgdb-container psql "postgres://your.username:rapsqladm@pgdb:5432/rapsql"
~~~ 

Once you are connected to postgres via CLI you can get information about your session by entering
~~~
\c
~~~ 

You can disconnect and quit this CLI by entering
~~~
\q
~~~ 

## Service Web Links ##
We can open some services in Browser. Maybe you need to change localhost to your machines host-ip or hostname, when using a remote system.

Swagger UI for documentation of PostgREST API

http://localhost:8080/


pgAdmin4 to interact with Postgres + Apache Age + PostgREST 
    
http://localhost:5556/


Apache AGE Viewer
    
http://localhost:3001/

## RAPSQL Features ##

### Cypher query via REST + JSON response ###
Hopefully everything is working properly now. We can use either curl or swagger if you have this service set up. Here's an example to interact with Swagger UI to perform cypher queries:

1. Open Swagger UI in browser http://localhost:8080/#/(rpc)%20rapsql_query/post_rpc_rapsql_query 
2. Push button 'Try it out' (on top right of this rpc)
3. For instance use the json body below and execute this query
~~~
{
    "querystring": "SELECT * FROM cypher('countries', $$ MATCH (e) RETURN properties(e) $$) AS (properties agtype) LIMIT 100;"
}
~~~ 
This function works for multiple rows of just one attribute (column) yet. I'm currently working on building multiple attributes by given agtypes to build a richer json response object.


### Further Development ###
The goal of this project is to evaluate drop-in replacements of graph databases with Postgres.

Progress of custum- features can be found in [./dev*](https://github.com/OpenSemanticLab/rapsql/tree/main/dev).

1. Function for JSON return by a dyanamic_string query as Remote Procedure Call (in work)
2. Research and implementation of a transpiler module for SPARQL to Cypher (to do)
3. Drop-in replacemant of an graph database (to do)


## Community ##
There is still one thing left... the support of the open source community! :heart:

Leave [Postgres](https://github.com/postgres/postgres), [Apache AGE](https://github.com/apache/age/), [Apache AGE Viewer](https://github.com/apache/age-viewer), [pgAdmin 4](https://github.com/pgadmin-org/pgadmin4), [PostgREST](https://github.com/PostgREST/postgrest), [Swagger-UI](https://github.com/swagger-api/swagger-ui) and [RAPSQL](https://github.com/OpenSemanticLab/rapsql/) a star if you like :star2: :heart_eyes:
