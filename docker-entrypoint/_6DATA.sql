-- Provide test data for automated API tests

-- SET search_path TO ag_catalog;
LOAD 'age';
SET search_path = ag_catalog, "$user", public;

-- CREATE a graph
SELECT * FROM ag_catalog.create_graph('api-test-data');


-- INSERT data into the graph
SELECT * FROM ag_catalog.cypher('api-test-data', $$ 
MERGE (s:Subject {uri: 'http://example.org/book/book2', stringrep: 'http://example.org/book/book2', value: 'http://example.org/book/book2'}) 
MERGE (o:Object {uri: '', typeiri: 'http://www.w3.org/2001/XMLSchema#integer', lexform: '23', stringrep: '23^^http://www.w3.org/2001/XMLSchema#integer', value: 23}) 
MERGE (s)-[:Predicate {uri: 'http://example.org/ns#price', stringrep: 'http://example.org/ns#price'}]->(o) 
$$) AS (n ag_catalog.agtype); 

SELECT * FROM ag_catalog.cypher('api-test-data', $$ 
MERGE (s:Subject {uri: 'http://example.org/book/book2', stringrep: 'http://example.org/book/book2', value: 'http://example.org/book/book2'}) 
MERGE (o:Object {uri: '', typeiri: 'http://www.w3.org/2001/XMLSchema#string', lexform: 'The Semantic Web', stringrep: 'The Semantic Web', value: 'The Semantic Web'}) 
MERGE (s)-[:Predicate {uri: 'http://purl.org/dc/elements/1.1/title', stringrep: 'http://purl.org/dc/elements/1.1/title'}]->(o) 
$$) AS (n ag_catalog.agtype); 

SELECT * FROM ag_catalog.cypher('api-test-data', $$ 
MERGE (s:Subject {uri: 'http://example.org/book/book1', stringrep: 'http://example.org/book/book1', value: 'http://example.org/book/book1'}) 
MERGE (o:Object {uri: '', typeiri: 'http://www.w3.org/2001/XMLSchema#integer', lexform: '42', stringrep: '42^^http://www.w3.org/2001/XMLSchema#integer', value: 42}) 
MERGE (s)-[:Predicate {uri: 'http://example.org/ns#price', stringrep: 'http://example.org/ns#price'}]->(o) $$) AS (n ag_catalog.agtype); 

SELECT * FROM ag_catalog.cypher('api-test-data', $$ 
MERGE (s:Subject {uri: 'http://example.org/book/book1', stringrep: 'http://example.org/book/book1', value: 'http://example.org/book/book1'}) 
MERGE (o:Object {uri: '', typeiri: 'http://www.w3.org/2001/XMLSchema#string', lexform: 'SPARQL Tutorial', stringrep: 'SPARQL Tutorial', value: 'SPARQL Tutorial'}) 
MERGE (s)-[:Predicate {uri: 'http://purl.org/dc/elements/1.1/title', stringrep: 'http://purl.org/dc/elements/1.1/title'}]->(o) 
$$) AS (n ag_catalog.agtype); 

-- Set permissions for the graph
GRANT ALL PRIVILEGES ON SCHEMA "api-test-data" TO web_anon;
GRANT ALL PRIVILEGES ON "api-test-data"._ag_label_vertex TO web_anon;
GRANT ALL PRIVILEGES ON "api-test-data"._ag_label_edge TO web_anon;

