/* schema.sql | Author(s): Andreas Raeder */

/*  Schema initialisation
    Author(s):  Andreas Raeder
    Hint(s):    Change this to your specific usecase and credentials in .env
    */
    \c rapsql;                            
    DROP DATABASE IF EXISTS postgres;
    DROP SCHEMA IF EXISTS public;
    CREATE SCHEMA api;
    /* End: Schema initialisation 
*/

/*  Post Installation and Importing graph from files
    Author(s):  Apache AGE
    Source(s):  https://age.apache.org/age-manual/master/intro/setup.html#post-installation
                https://age.apache.org/age-manual/master/intro/agload.html#importing-graph-from-files
    Modified:   Andreas Raeder 
    Hint(s):    This a docker specific .csv path for rapsql-stack 
    */
    -- Post Installation
    LOAD 'age';
    SET search_path = ag_catalog, "$user", public;
    
    -- Importing graph from files
    SELECT create_graph('countries');
    SELECT create_vlabel('countries','Country');
    SELECT load_labels_from_file('countries',
                                'Country',
                                '/age/regress/age_load/data/countries.csv');

    SELECT create_vlabel('countries','City');
    SELECT load_labels_from_file('countries',
                                'City', 
                                '/age/regress/age_load/data/cities.csv');

    SELECT create_elabel('countries','has_city');
    SELECT load_edges_from_file('countries', 
                                'has_city',
                                '/age/regress/age_load/data/edges.csv');

    SELECT table_catalog, table_schema, table_name, table_type
    FROM information_schema.tables
    WHERE table_schema = 'countries';

    SELECT COUNT(*) FROM countries."Country";
    SELECT COUNT(*) FROM countries."City";
    SELECT COUNT(*) FROM countries."has_city";

    SELECT COUNT(*) FROM cypher('countries', $$MATCH(n) RETURN n$$) as (n agtype);
    SELECT COUNT(*) FROM cypher('countries', $$MATCH (a)-[e]->(b) RETURN e$$) as (n agtype);
    /* End: Post Installation and Importing graph from files
*/

/*  Automatic Schema Cache Reloading
    Author(s):  Joe Nelson, Steve Chavez Revision 96e98194
    Source(s):  https://postgrest.org/en/v8.0/schema_cache.html#automatic-schema-cache-reloading
    Modified:   Andreas Raeder 
    */
    -- Create an event trigger function
    CREATE OR REPLACE FUNCTION api.pgrst_watch() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
    NOTIFY pgrst, 'reload schema';
    END;
    $$;
    -- This event trigger will fire after every ddl_command_end event
    CREATE EVENT TRIGGER pgrst_watch
    ON ddl_command_end
    EXECUTE PROCEDURE api.pgrst_watch();
    /* End: Automatic Schema Cache Reloading 
*/

/*  Access control
    Author(s):  Andreas Raeder
    Hint(s):    ATTENTION - UNSAFE USER SETTINGS! USE FOR DEV PURPOSES ONLY!
    */
    -- GRANT USAGE ON SCHEMA ag_catalog TO your.username; -- change your.username
    CREATE ROLE web_anon nologin;
    GRANT USAGE ON SCHEMA api TO web_anon;
    CREATE ROLE authenticator NOINHERIT LOGIN PASSWORD 'pgrestauth'; -- pw see .env
    GRANT web_anon TO authenticator;
    GRANT ALL PRIVILEGES ON DATABASE rapsql TO web_anon;
    GRANT USAGE ON SCHEMA countries TO web_anon;
    GRANT ALL PRIVILEGES ON SCHEMA ag_catalog TO web_anon;
    GRANT SELECT ON countries._ag_label_vertex TO web_anon;
    GRANT SELECT ON countries._ag_label_edge TO web_anon;
    GRANT SELECT ON countries."Country" TO web_anon;
    GRANT SELECT ON countries."City" TO web_anon;
    GRANT SELECT ON countries."has_city" TO web_anon;
    /* End: Access control 
*/

/*  Function api.rapsql_query(querystring VARCHAR)
    Author(s):  Andreas Raeder
    Source(s):  https://github.com/OpenSemanticLab/rapsql/blob/main/dev/sql/rap.sql 
    Version:    0.1.3
    */
    CREATE OR REPLACE FUNCTION api.rapsql_query(querystring VARCHAR)
    RETURNS SETOF JSON
    LANGUAGE plpgsql
    AS $function$
    DECLARE 
        sql_agtype VARCHAR;
    BEGIN
        SET search_path TO ag_catalog;
        sql_agtype := format('SELECT (to_json(replace(replace(replace(replace(replace(replace(replace(replace((row_to_json(t))::text,''}::vertex'', '', "_type":"::vertex"}''), ''}::edge'', '', "_type":"::edge"}''), ''::path'', '', "_type":"::path"''), ''"['', ''[''), ''\"'', ''"''), ''"{'', ''{''), ''}"'', ''}''), ''""'', ''"'')) #>> ''{}'')::JSON FROM (%s) t;', querystring);
        RETURN QUERY EXECUTE sql_agtype;
    END
    $function$;
    /* End: Function api.rapsql_query(querystring VARCHAR) 
*/

