-- REST API settings (!not ready for production)

-- setup sqlj permission for postgrest api (!insecure)
GRANT ALL PRIVILEGES ON SCHEMA sqlj TO web_anon;

-- set search path to schema api for postgrest endpoint deployment
SET search_path = "$user", api;

-- !update reqired on new versions in pom.xml
-- install transpiler and provide annotation function endpoints 
SELECT sqlj.install_jar( 'file:///tmp/s2agec/target/s2agec-0.1.0-jar-with-dependencies.jar', 
                          's2agec', true);
SELECT sqlj.set_classpath( 'api', 's2agec');

-- sparql endpoint 
CREATE OR REPLACE FUNCTION api.rapsql_sparql(
  graph_name pg_catalog.varchar, 
  sparql_query pg_catalog.varchar)
RETURNS SETOF JSON
LANGUAGE java VOLATILE
AS 'java.lang.String=de.rapsql.s2agec.PostgresApi.rapsql_sparql( java.lang.String,java.lang.String)';

-- cypher endpoint 
CREATE OR REPLACE FUNCTION api.rapsql_cypher(cypher_query pg_catalog.varchar)
RETURNS SETOF JSON
LANGUAGE java VOLATILE
AS 'java.lang.String=de.rapsql.s2agec.PostgresApi.rapsql_cypher( java.lang.String)';

/*  
  Function api.rapsql_query(querystring VARCHAR)
  Author(s):  Andreas Raeder
  Source(s):  https://github.com/OpenSemanticLab/rapsql/blob/main/dev/sql/rap.sql 
  Version:    0.1.4
  Dscpt.:     Query endpoint for lean answers directly on the server side, 
              returning only keys and values of table attributes and data 
              for AGE Cypher, SQL or hybrid queries. 
              It uses built-in pg_catalog functions to return valid json objects 
              and applies multiple substitutions for type escaping of ag_type (see URL).
  */
-- Enhanced query execution endpoint
CREATE OR REPLACE FUNCTION api.rapsql_query(querystring VARCHAR)
RETURNS SETOF JSON
LANGUAGE plpgsql
AS $function$
DECLARE 
    sql_agtype VARCHAR;
BEGIN
    SET search_path TO ag_catalog;
    sql_agtype := format('SELECT (to_json(replace(replace(replace(replace(replace(replace(replace(replace( (row_to_json(t))::text,''}::vertex'', '', "_type":"::vertex"}''), ''}::edge'', '', "_type":"::edge"}''), ''::path'', '', "_type":"::path"''), ''"['', ''[''), ''\"'', ''"''), ''"{'', ''{''), ''}"'', ''}''), ''""'', ''"'')) #>> ''{}'')::JSON FROM (%s) t', querystring);
    RETURN QUERY EXECUTE sql_agtype;
END
$function$;
  /* End: Function api.rapsql_query(querystring VARCHAR) 
*/

-- provide age drop_graph() endpoint 
CREATE OR REPLACE FUNCTION api.graph_delete(graph_name VARCHAR)
  RETURNS VARCHAR
  LANGUAGE plpgsql
  AS $$
  DECLARE
    result_message VARCHAR;
  BEGIN
    SELECT INTO result_message 'graph "' || graph_name || '" has been dropped';
    PERFORM ag_catalog.drop_graph(graph_name, true);
    RETURN result_message;
  END;
$$;

-- provide age create_graph() endpoint 
CREATE OR REPLACE FUNCTION api.graph_create(graph_name VARCHAR)
  RETURNS VARCHAR
  LANGUAGE plpgsql
  AS $$
  DECLARE
    result_message VARCHAR;
  BEGIN
    SELECT INTO result_message 'graph "' || graph_name || '" has been created';
    PERFORM ag_catalog.create_graph(graph_name);
    RETURN result_message;
  END;
  $$;

/*  JWT AND RDF ENDPOINT ACCESS CURRENTLY IN DEVELOPMENT 
  -- permission settings (! write access for api.rapsql_rdf())
  
CREATE OR REPLACE FUNCTION rapsql_rdf(
  graph_name pg_catalog.varchar, 
  lang_str pg_catalog.varchar,
  rdf_str pg_catalog.varchar)
RETURNS SETOF JSON
LANGUAGE java 
-- LANGUAGE java VOLATILE
AS 'java.lang.String=de.rapsql.s2agec.PostgresApi.rapsql_rdf( java.lang.String,java.lang.String,java.lang.String)';
*/