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

import org.postgresql.pljava.annotation.Function;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.Algebra;

public class SparqlToCypher2 {
  @Function // auto generated endpoint via _5API.sql
  public static String rapsql_s2c(String graph_name, String sparql_query) throws QueryException {
    // compile sparql query string using jena algebra
    Query sq = QueryFactory.create(sparql_query);
    Op op = Algebra.compile(sq);
    // create visitor instance for compiler access
    SparqlAlgebraVisit2 visitor = new SparqlAlgebraVisit2();
    op.visit(visitor);                                    // transformation here
    String cypher_visit = visitor.getCypher();            // transformed content
    // build cypher using age syntax and visitor content
    String cypher = "SELECT * FROM ag_catalog.cypher('"
                    + graph_name + "', $$ " + cypher_visit;
    return (cypher);
  }
}