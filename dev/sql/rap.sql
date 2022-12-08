/* rap.sql | Author(s): Andreas Raeder | Version 0.1.3 */

/*  Function api.rapsql_query(querystring VARCHAR)
    Author(s):  Andreas Raeder
    Version:    0.1.1
    Descript.:  Postgrest RPC for json return by using dynamic apache age cypher query string
    Hint(s):    No permission to "LOAD 'age';" for REST API yet
                Can return multiple JSON results of one attribute yet
    ToDo(s):    Specifiy permissions
                Implementation of multiple attributes including nested JSON object refered to agtype return
    Usage:      POST request e. g. on swagger ui, example JSON body          
                    {
                        "querystring": "SELECT * FROM cypher('countries', $$ MATCH (e) RETURN properties(e) $$) AS (properties agtype) LIMIT 20;"
                    }
    */
    CREATE OR REPLACE FUNCTION api.rapsql_query(querystring VARCHAR)
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
    /* End: Function api.rapsql_query(querystring VARCHAR) 
*/

