-- permission setup, !not for production
LOAD 'age';
SET search_path = ag_catalog, "$user", public;

-- setup api web_anon and authenticator role, !not for production
CREATE ROLE web_anon nologin;
GRANT USAGE ON SCHEMA api TO web_anon;
CREATE ROLE authenticator NOINHERIT LOGIN PASSWORD 'pgrestauth'; 
GRANT web_anon TO authenticator;
GRANT ALL PRIVILEGES ON DATABASE rapsql TO web_anon; 

-- set permissions on graph 'countries' for postgrest api 
GRANT USAGE ON SCHEMA countries TO web_anon;
GRANT ALL PRIVILEGES ON SCHEMA ag_catalog TO web_anon;
GRANT SELECT ON countries._ag_label_vertex TO web_anon;
GRANT SELECT ON countries._ag_label_edge TO web_anon;
GRANT SELECT ON countries."Country" TO web_anon;
GRANT SELECT ON countries."City" TO web_anon;
GRANT SELECT ON countries."has_city" TO web_anon;

-- set permissions on graph 'rdf' for postgrest api 
GRANT ALL PRIVILEGES ON SCHEMA rdf TO web_anon;
GRANT SELECT ON rdf._ag_label_vertex TO web_anon;
GRANT SELECT ON rdf._ag_label_edge TO web_anon;

/* ! jwt access in development for rapsql_rdf() endpoint, 
ref. PostgresApi.java */
-- auth jwt config for users (example token see .env)
CREATE ROLE api_user nologin;
GRANT api_user TO authenticator;
GRANT usage ON SCHEMA api TO api_user;

GRANT ALL PRIVILEGES ON SCHEMA ag_catalog TO api_user;
GRANT ALL PRIVILEGES ON DATABASE rapsql TO api_user;
/* jwt table access example
-- set permissions for on tables this way --
  grant all on api.<table_name> to api_user;   
  grant usage, select on sequence api.<table_name>_id_seq to api_user;
*/
-- permission settings (!debug write access for api.rapsql_rdf())
-- GRANT USAGE ON SCHEMA ag_catalog TO web_anon;
-- GRANT USAGE ON SCHEMA ag_catalog TO authenticator;
/* ! */