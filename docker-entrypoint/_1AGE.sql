-- init apache age
\c rapsql;   
CREATE EXTENSION IF NOT EXISTS age;                       
DROP DATABASE IF EXISTS postgres;
DROP SCHEMA IF EXISTS public;

-- post installation
LOAD 'age';
SET search_path = ag_catalog, "$user", public;

-- create test graph 'countries' and import datasets from csv files
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

-- create empty test graph 'rdf' for transpiler and api tests
SELECT create_graph('rdf');