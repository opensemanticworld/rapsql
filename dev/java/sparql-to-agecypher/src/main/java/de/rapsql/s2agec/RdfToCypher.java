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

package de.rapsql.s2agec;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.Statement;
import org.postgresql.pljava.annotation.Function;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFLanguages;

public class RdfToCypher {

  // rdf mapping into age-specific cypher using MERGE for every triple
  public static String rdfToCypher(String graph_name, Model model) {
    String cypher_qs = "";
    // iterate over all rdf model statements
    StmtIterator it =  model.listStatements();
    while (it.hasNext()) {
        Statement stmt = it.next();
        String cypher_q = "";
        // get triple data from rdf visitor and build age syntax of MERGE
        RdfVisit visitor = new RdfVisit("MERGE");
        cypher_q = cypher_q.concat("SELECT * FROM ag_catalog.cypher('" 
                                    + graph_name + "', $$ ");
        visitor.set_label("s:Subject");
        cypher_q = cypher_q.concat((String) stmt.getSubject().visitWith(visitor));
        cypher_q = cypher_q.concat(" ");
        visitor.set_label("o:Object");
        cypher_q = cypher_q.concat((String) stmt.getObject().visitWith(visitor));
        cypher_q = cypher_q.concat(
          String.format(" MERGE (s)-[:Predicate {uri: '%s', stringrep: '%s'}]->(o) $$) AS (n ag_catalog.agtype); ", 
          stmt.getPredicate().getURI(), stmt.getPredicate().getURI()));
        cypher_qs = cypher_qs.concat(cypher_q);
    }
    return cypher_qs;
  }

  // used to investigate any rdf types
  @Function // auto generated endpoint via _5API.sql
  public static String rapsql_r2c(String graph_name, String lang_str, String rdf_str) throws QueryException {
    Model model = RDFParser
                    .create()
                    .fromString(rdf_str)
                    .lang(RDFLanguages.nameToLang(lang_str))
                    .toModel();
    String cypher_str = rdfToCypher(graph_name, model);
    return cypher_str;
  }
  // FUTURE WORK, integrate https://github.com/renzoar/rdf2pg
  // TODO test any other rdf model beside ttl
  // https://json-ld.github.io/json-ld-star/
  // https://jena.apache.org/tutorials/rdf_api.html
}