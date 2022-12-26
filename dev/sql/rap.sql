/* rap.sql | Author(s): Andreas Raeder | Version 0.1.3 */

/*  Function api.rapsql_query(querystring VARCHAR)
    Author(s):  Andreas Raeder
    Version:    0.1.3
    Descript.:  
                Postgrest RPC for json return by using dynamic apache age cypher query string; 
                Can return multiple JSON results of multiple attributes (experimental) 
    ToDo(s):    
                Testing different edge cases
                Maybe improvement by type cast
                Check responses with "::vertex, ::edge, ::path" ending
    Usage:      
                {
                    "querystring": "SELECT * FROM cypher('countries', $$ MATCH (e) RETURN properties(e) $$) AS (properties agtype) LIMIT 3"
                }
                {
                    "querystring": "SELECT * FROM cypher('countries', $$ MATCH (country {name: 'Germany'})<-[r]-(city) RETURN properties(country), city.name $$) as (country agtype, city agtype) LIMIT 3"
                }
    
    */
    DROP FUNCTION IF EXISTS api.rapsql_query(querystring VARCHAR);

    CREATE OR REPLACE FUNCTION api.rapsql_query(querystring VARCHAR)
    RETURNS SETOF JSON
    LANGUAGE plpgsql
    AS $function$
    DECLARE 
        sql_agtype VARCHAR;
    BEGIN
        SET search_path TO ag_catalog;
        sql_agtype := format('SELECT (to_json(replace(replace(replace(replace((row_to_json(t))::text, ''\"'', ''"''), ''"{'', ''{''), ''}"'', ''}''), ''""'', ''"'')) #>> ''{}'')::JSON FROM (%s) t;', querystring);
        RETURN QUERY EXECUTE sql_agtype;
    END
    $function$;
    /* End: Function api.rapsql_query(querystring VARCHAR) 
*/

/*  Function api.rapsql_query2(querystring VARCHAR)
    Author(s):  Andreas Raeder
    Version:    0.1.2
    Descript.:  
                Postgrest RPC for json return by using dynamic apache age cypher query string; 
                Can return multiple JSON results of multiple attributes, but cypher response is escaped
    Usage:      
                {
                    "querystring": "SELECT * FROM cypher('countries', $$ MATCH (e) RETURN properties(e) $$) AS (properties agtype) LIMIT 3"
                }
                {
                    "querystring": "SELECT * FROM cypher('countries', $$ MATCH (country {name: 'Germany'})<-[r]-(city) RETURN properties(country), city.name $$) as (countries agtype, cities agtype) LIMIT 3"
                }
    
    */
    DROP FUNCTION IF EXISTS api.rapsql_query2(querystring VARCHAR);

    CREATE OR REPLACE FUNCTION api.rapsql_query2(querystring VARCHAR)
    RETURNS SETOF JSON
    LANGUAGE plpgsql
    AS $function$
    DECLARE 
        sql_agtype VARCHAR;
        sql_agtype_output ag_catalog.agtype;
        _result JSON;
    BEGIN
        SET search_path TO ag_catalog;
        sql_agtype := format('SELECT row_to_json((t.*) FROM (%s)  t;', querystring);
        RETURN QUERY EXECUTE sql_agtype;
    END
    $function$;
    /* End: Function api.rapsql_query2(querystring VARCHAR) 
*/

/*  Function api.rapsql_query1(querystring VARCHAR)
    Author(s):  Andreas Raeder
    Version:    0.1.1
    Descript.:  
                Postgrest RPC for json return by using dynamic apache age cypher query string; 
                Can return multiple JSON results of one attribute yet
    Hint(s):    
                No permission to "LOAD 'age';" for REST API yet
    ToDo(s):    
                Specifiy permissions
                Implementation of multiple attributes including nested JSON object refered to agtype return
    Usage:      
                POST request e. g. on swagger ui, example JSON body          
                {
                    "querystring": "SELECT * FROM cypher('countries', $$ MATCH (e) RETURN properties(e) $$) AS (properties agtype) LIMIT 20;"
                }
    */
    DROP FUNCTION IF EXISTS api.rapsql_query1(querystring VARCHAR);        

    CREATE OR REPLACE FUNCTION api.rapsql_query1(querystring VARCHAR)
    RETURNS SETOF JSON
    LANGUAGE plpgsql
    AS $function$
    DECLARE 
        sql_agtype VARCHAR;
        sql_agtype_output ag_catalog.agtype;
        _result JSON;
    BEGIN
        -- LOAD 'age'; 
        SET search_path TO ag_catalog;
        sql_agtype := format('%s', querystring);
        FOR sql_agtype_output IN EXECUTE sql_agtype 
        LOOP 
            SELECT (to_json(sql_agtype_output) #>> '{}')::JSON INTO _result;
            RETURN NEXT _result;
        END LOOP;
        RETURN;
    END
    $function$;
    /* End: Function api.rapsql_query1(querystring VARCHAR) 
*/

