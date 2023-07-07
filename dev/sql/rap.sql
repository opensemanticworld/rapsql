/* 
   Copyright 2023 Andreas Räder

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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
    sql_agtype := format('SELECT (to_json(replace(replace(replace(replace(replace(replace(replace(replace((row_to_json(t))::text,''}::vertex'', '', "_type":"::vertex"}''), ''}::edge'', '', "_type":"::edge"}''), ''::path'', '', "_type":"::path"''), ''"['', ''[''), ''\"'', ''"''), ''"{'', ''{''), ''}"'', ''}''), ''""'', ''"'')) #>> ''{}'')::JSON FROM (%s) t;', querystring);
    RETURN QUERY EXECUTE sql_agtype;
END
$function$;

/*  
    Function api.rapsql_query(querystring VARCHAR)
    Author(s):  Andreas Raeder
    Version:    0.1.4 (latest)
    Descript.:  
                Experimental User Defined Function (UDF) to build valid JSON objects from any AGE cypher query;
                Postgrest RPC for JSON return by using dynamic Apache AGE cypher query string; 
                Can return multiple JSON results of multiple attributes and rows;
                Different agtype's like ::vertex, ::edge, or ::path will be expanded in JSON as _type result;
    Usage:      
                Directed relation, n attributes
                    {
                        "querystring": "SELECT * FROM cypher('countries', $$ MATCH (country {currency: 'EUR'})<-[r]-(city) RETURN country.name, city.name $$) as (_country agtype, _city agtype) LIMIT 100"
                    }

                AGE type ::vertex
                    {
                        "querystring": "SELECT * FROM cypher('countries', $$ MATCH (v) RETURN (v) $$) as (_result ag_catalog.agtype) LIMIT 3"
                    }

                AGE type ::edge
                    {
                        "querystring": "SELECT * FROM cypher('countries', $$ MATCH (country)<-[e]-(city) RETURN (e) $$) as (_result agtype) LIMIT 3"
                    }

                AGE type ::path (Modified test code: https://age.apache.org/age-manual/master/intro/types.html#path)    
                    {
                        "querystring": "SELECT * FROM cypher('countries', $$ WITH [{id: 0, label: 'label_name_1', properties: {i: 0}}::vertex, {id: 2, start_id: 0, end_id: 1, label: 'edge_label', properties: {i: 0}}::edge, {id: 1, label: 'label_name_2', properties: {}}::vertex]::path as p RETURN p $$) AS (_result agtype)"
                    }
*/