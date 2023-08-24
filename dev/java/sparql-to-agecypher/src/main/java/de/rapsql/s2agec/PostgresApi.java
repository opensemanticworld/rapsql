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

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class PostgresApi {
  // ///////////////  SOURCE CODE OF _5API.SQL METHODS ///////////////
           
  // ///////////////  CURRENT DEVELOPMENT BELOW //////////////////////
  // ! rapsql_rdf endpoint in development for any rdf data type 
  // ! integrate in _5API.sql after successful implementation 
  // TODO: overload function by adding ( String conn_str ) 
      // as input parameter for debugging in Helper.java locally 
  // TODO: check access rights on sqlj scheme (pljava)
  // TODO: check access using JWTs and web_anon (postgrest)
  // TODO: further, create keycloack JWT provider for stack in general

  // (! dev) rdf import endpoint
  public static Iterator<JsonValue> rapsql_rdf(String graph_name, String lang_str, String rdf_str ) throws SQLException {
    // create json for return value
    JsonArray json_arr = new JsonArray();       // json data
    JsonObject json_obj = new JsonObject();
    // create rdf model and parse as cypher statement string
    String rdf_to_cypher = new String();
    try { 
      rdf_to_cypher = RdfToCypher.rapsql_r2c(graph_name, lang_str, rdf_str); 
      json_arr.add(0, json_obj.put("import", rdf_to_cypher));
    } 
    catch (QueryException e) { e.printStackTrace(); return json_arr.iterator(); }

    // execute import statement 
    // default conn for endpoint / credentials for local debugging
    try ( Connection conn = DriverManager.getConnection("jdbc:default:connection") ) {  
    // try ( Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/rapsql", "postgres", "postgres") ) {
    Statement stmt = conn.createStatement(); 
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");     
      stmt.execute(rdf_to_cypher);         
    } // handle sql errors
    catch (SQLException e) { e.printStackTrace(); return json_arr.iterator(); }
    return json_arr.iterator();
  }

  // ///////////////////  CURRENT DEVELOPMENT ABOVE //////////////////

  // ///////////////////  WORKING ENDPOINTS DOWN HERE //////////////// 

  // sparql query endpoint 
  public static Iterator<JsonValue> rapsql_sparql(String graph_name, String sparql_query) throws SQLException {
    String cypher_query = new String();
    JsonArray json_arr = new JsonArray();       // json data
    JsonObject json_obj = new JsonObject();
    JsonObject vars_obj = new JsonObject();     // json head
    JsonArray vars_arr = new JsonArray();
    JsonObject bindings_obj = new JsonObject(); // json results
    JsonArray bindings_arr = new JsonArray();

    // transform sparql to age cypher
    // difference to cypher endpoint: rapsql_s2c() call needs input graph_name
    try { cypher_query = SparqlToCypher.rapsql_s2c(graph_name, sparql_query); } 
    catch (QueryException e) { e.printStackTrace(); }

    // run age cypher and build response by default jdbc connection
    try (Connection conn = DriverManager.getConnection("jdbc:default:connection")) {
      Statement stmt = conn.createStatement(); 
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");  
      ResultSet rs = stmt.executeQuery(cypher_query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      // json head vars content
      for (int i=1; i<=columnsNumber; i++) { vars_arr.add(rsmd.getColumnName(i)); }
      // json results bindings content
      while (rs.next()) {
        JsonObject bindings_arr_obj = new JsonObject();
        for (int i = 1; i <= columnsNumber; i++) {
          JsonObject val_obj = new JsonObject();
          // split datatype and value
          String value = rs.getObject(i).toString().replace("\"", "");
          if (value.contains("^^")) {
            ArrayList<String> split = new ArrayList<String>(Arrays.asList(value.split("\\^\\^")));
            val_obj.put("datatype", split.get(1));
            val_obj.put("type", "literal"); 
            val_obj.put("value", split.get(0));
          } else { val_obj.put("type", "literal"); val_obj.put("value", value);}
          bindings_arr_obj.put(rsmd.getColumnName(i), val_obj);
        }
        bindings_arr.add(bindings_arr_obj);
      }
    } catch (SQLException e) { e.printStackTrace(); }

    // build json object as array
    vars_obj.put("vars", vars_arr);
    bindings_obj.put("bindings", bindings_arr);
    json_obj.put("head", vars_obj);
    json_obj.put("results", bindings_obj);
    json_arr.add(json_obj);
    return json_arr.iterator();
  }

  // cypher query endpoint (cypher_query provides graph_name)
  public static Iterator<JsonValue> rapsql_cypher(String cypher_query) throws SQLException {
    JsonArray json_arr = new JsonArray();       // json data
    JsonObject json_obj = new JsonObject();
    JsonObject vars_obj = new JsonObject();     // json head
    JsonArray vars_arr = new JsonArray();
    JsonObject bindings_obj = new JsonObject(); // json results
    JsonArray bindings_arr = new JsonArray();

    // default conn for endpoint / credentials for local debugging
    try (Connection conn = DriverManager.getConnection("jdbc:default:connection")) {
      Statement stmt = conn.createStatement(); 
      stmt.execute("SET search_path = ag_catalog, \"$user\", public;");           
      ResultSet rs = stmt.executeQuery(cypher_query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      // json head vars content
      for (int i = 1; i <= columnsNumber; i++) {
        vars_arr.add(rsmd.getColumnName(i));
      }

      // json bindings content
      while (rs.next()) {
        JsonObject bindings_arr_obj = new JsonObject();
        for (int i = 1; i <= columnsNumber; i++) {
          JsonObject val_obj = new JsonObject();
          // split datatype and value
          String value = rs.getObject(i).toString().replace("\"", "");
          if (value.contains("^^")) {
            ArrayList<String> split = new ArrayList<String>( Arrays.asList(value.split("\\^\\^")) );
            val_obj.put("datatype", split.get(1));
            // TODO : static type value "literal" -> dynamic tbd 
            val_obj.put("type", "literal"); 
            val_obj.put("value", split.get(0));
          } else {
            // TODO : static type value "literal" -> dynamic tbd
            val_obj.put("type", "literal"); 
            val_obj.put("value", value);
          }
          bindings_arr_obj.put(rsmd.getColumnName(i), val_obj);
        }
        bindings_arr.add(bindings_arr_obj);
      }
  
    } catch (SQLException e) { e.printStackTrace(); }

    // build json object as array
    vars_obj.put("vars", vars_arr);
    bindings_obj.put("bindings", bindings_arr);
    json_obj.put("head", vars_obj);
    json_obj.put("results", bindings_obj);
    json_arr.add(json_obj);
    return json_arr.iterator();
  }

    // ////////////////// END OF JAVA API METHODS  //////////////////
}
