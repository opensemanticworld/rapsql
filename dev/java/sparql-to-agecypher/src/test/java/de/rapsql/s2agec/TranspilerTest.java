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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;


import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TranspilerTest {

  private static final String GRAPH_NAME = "junit-test";
  private static final String PATH_NAME = "src/test/resources/ttl-sparql";

  @Container
  public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(
    DockerImageName.parse("apache/age:v1.1.0")
    .asCompatibleSubstituteFor("postgres")
  )
    .withDatabaseName("postgres")
    .withUsername("postgres")
    .withPassword("postgres");

  // // provide test resources of rdf model, rdf-cypher model, sparql queries
  private static List<Arguments> MethodProvider() throws IOException {
    // provide all folder structured ttl and sparql files
    File f = new File(PATH_NAME);
    FileFilter directoryFilter = new FileFilter() {
      public boolean accept(File file) { return file.isDirectory();	}
    };
    File[] files = f.listFiles(directoryFilter);
    Arrays.sort(files); // sort .ttl model files
    List<Arguments> method_args = new LinkedList<Arguments>();
    for (File folder_ : files) {
      String folder = folder_.getCanonicalPath();
      Path rdf_path = Paths.get(folder, "rdf.ttl");
            
      // create RDF to Cypher model from ttl
      Model rdf_model = RDFDataMgr.loadModel(rdf_path.toString());
      String rdf_to_cypher = RdfToCypher.rdfToCypher(GRAPH_NAME, rdf_model);  
      
      // iterate over all sparql files in all query folders
      FileFilter sparqlFilter = new FileFilter() {
        public boolean accept(File file) {
          String extension = "";
          int i = file.getName().lastIndexOf('.');
          if (i > 0) { extension = file.getName().substring(i+1); }
          return file.isFile() && (extension.equals("sparql"));
        }
      };
      File[] query_files = Paths.get(folder, "queries").toFile().listFiles(sparqlFilter);
      Arrays.sort(query_files); // sort .sparql query files
      for(File query_file: query_files) {
        method_args.add(Arguments.of(rdf_model, rdf_to_cypher, query_file));
      }
    }
    return method_args;
  }



  @BeforeEach // create test graph in postgres
  public void age_create_graph() throws SQLException {
    System.out.println(StringUtils.repeat("%", 70));
    try ( Connection conn = DriverManager.getConnection(
        db.getJdbcUrl(), db.getUsername(), db.getPassword()
      ) 
    ) {
      Statement stmt = conn.createStatement(); 
      stmt.execute("LOAD 'age';");
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
      stmt.execute("SELECT * FROM ag_catalog.create_graph('" + GRAPH_NAME  + "');");
      System.out.println("-------- AGE TEST GRAPH CREATED --------");
    } catch (SQLException e) { e.printStackTrace(); }
  }

  @AfterEach // drop test graph in postgres
  public void age_drop_graph() throws SQLException {
    try ( Connection conn = DriverManager.getConnection(
        db.getJdbcUrl(), db.getUsername(), db.getPassword()
      ) 
    ) {      Statement stmt = conn.createStatement(); 
      stmt.execute("LOAD 'age'");
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
      stmt.execute("SELECT * FROM drop_graph('" + GRAPH_NAME  + "', true);");
      System.out.println("-------- AGE TEST GRAPH DELETED --------\n");
    } catch (SQLException e) { e.printStackTrace(); }    
  }

  @ParameterizedTest // equality tests of both sparql and agecypher results
  @MethodSource("MethodProvider")
  public void run_test(Model rdf_model, String rdf_to_cypher, File query_file) throws IOException {
    // import custom rdf model to age
    try ( Connection conn = DriverManager.getConnection(
        db.getJdbcUrl(), db.getUsername(), db.getPassword()
      ) 
    ) {      
      Statement stmt = conn.createStatement(); 
      stmt.execute("LOAD 'age';");
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
      stmt.execute(rdf_to_cypher); // rdf to cypher data mapping here
      System.out.println("-------- SUCCESS AGE RDF IMPORT  --------\n");
    } catch (SQLException e) { 
      e.printStackTrace(); 
      fail("Error: RDF-Cypher-Mapping query in PostgreSQL AGE");
      return; 
    }

    // define result map of sparql and cypher results
    Set<Map<String, String>> sparql_res_map = new HashSet<Map<String, String>>();
    Set<Map<String, String>> cypher_res_map = new HashSet<Map<String, String>>();

    // print information about sparql path and query
    String sparql_query = null;
    try {
      sparql_query = new String(Files.readAllBytes(query_file.toPath()));
      System.out.println("-------- SPARQL QUERY --------\n"+ sparql_query + "\n");
    } catch (IOException e) { e.printStackTrace(); }

    // build-in query execution of jena library
    Query query = QueryFactory.create(sparql_query);
    QueryExecution qe = QueryExecutionFactory.create(query, rdf_model);
    org.apache.jena.query.ResultSet results = qe.execSelect(); // sparql result here
    // sparql result set mapping 
    while(results.hasNext()) {
      Map<String, String> sparql_res = new HashMap<String, String>();
      QuerySolution row = results.next();
      for(String col: results.getResultVars()) {
        sparql_res.put(col, row.get(col).toString());
      }
      sparql_res_map.add(sparql_res); // add result to sparql result list
    }

    // transform and print sparql to cypher query
    String cypher_query = null;
    try {
      cypher_query = SparqlToCypher.rapsql_s2c(GRAPH_NAME, sparql_query);
      System.out.println("-------- CYPHER QUERY --------\n" + cypher_query + "\n");
    } catch (QueryException e) {
      System.err.println("-------- CYPHER QUERY --------\n");
      System.err.println("S2C SKIPPED: " + e.getMessage()); 
      Assumptions.assumeTrue(false, e.getMessage());
      return;
    }

    // connect to postgres to perform test on transformed cypher query
    try ( Connection conn = DriverManager.getConnection(
        db.getJdbcUrl(), db.getUsername(), db.getPassword()
      ) 
    ) {      
      Statement stmt = conn.createStatement(); 
      stmt.execute("LOAD 'age'");
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");             
      ResultSet rs = stmt.executeQuery(cypher_query); // sparql-to-cypher result here
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      // create map of single cypher statements 
      while (rs.next()) {
        Map<String, String> res = new HashMap<String, String>();
        for (int i = 1; i <= columnsNumber; i++) {
          res.put(rsmd.getColumnName(i), rs.getString(i).replace("\"", ""));
        }
        cypher_res_map.add(res);
      }
    } catch (SQLException e) { 
      fail("CYPHER FAILED WITH EXCEPTION\n"
        + e.getStackTrace()
        + "\nSPARQL QUERY:\n" + sparql_query
        + "\n\nCYPHER QUERY:\n" + cypher_query
      );
    }

    /*    EQUALITY TEST    */
    // print sparql and cypher result maps for test run
    System.out.println(StringUtils.repeat("-", 40));
    System.out.println("SPARQL RESULT: " + sparql_res_map);
    System.out.println("\nCYPHER RESULT: " + cypher_res_map);
    System.out.println(StringUtils.repeat("-", 40));
    // testing equality of sparql and cypher result maps
    assertEquals(sparql_res_map, cypher_res_map, String.format(    
      "Equality test failed for %s\nSparql result:\n%s\n\nCypher Result\n%s", 
      query_file.getCanonicalPath(),
      sparql_res_map.toString(),
      cypher_res_map.toString()
    ));
  }

  // TODO: automate API testing 
  // see also todos in PostgresApi.java
}