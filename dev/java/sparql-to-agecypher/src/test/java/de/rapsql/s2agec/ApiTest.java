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

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest {
  final static private String GRAPH_NAME = "api-test";
  final static private String PREPARED_DATA = "api-test-data";
  private static final String URL = System.getProperty(
    "api.url", "http://localhost:3000"
  );

  public void test_response_code(Response response) {
    assertEquals(
      200, 
      response.getStatusCode(), 
      "Response status code: " + response.getStatusCode()
    );
  }

  public Response rapsql_rpc_res(String endpoint, JsonObject payload) {
    return given()
            .baseUri(URL)
            .contentType(ContentType.JSON)
            .body(payload.toString())
            .when()
            .post("/rpc" + endpoint);
  }

  @Test
  @Order(1)
  public void test_graph_create(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("graph_name", GRAPH_NAME);

    Response response = rapsql_rpc_res("/graph_create", payload);
    test_response_code(response);
  }

  @Test
  @Order(2)
  public void test_graph_delete(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("graph_name", GRAPH_NAME);

    Response response = rapsql_rpc_res("/graph_delete", payload);
    test_response_code(response);
  }

  @Test
  @Order(3)
  public void test_rapsql_query(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("querystring", "SELECT * FROM ag_catalog.cypher('api-test-data', $$ MATCH (x)-[{uri:'http://example.org/ns#price'}]->(price),(x)-[{uri:'http://purl.org/dc/elements/1.1/title'}]->(title) WHERE price.value < 30.5 RETURN title.stringrep, price.stringrep $$) AS (title ag_catalog.agtype, price ag_catalog.agtype)");

    Response response = rapsql_rpc_res("/rapsql_query", payload);
    test_response_code(response);

  }

  @Test
  @Order(4)
  public void test_rapsql_s2c(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("graph_name", PREPARED_DATA);
    payload.put("sparql_query", "PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX ns: <http://example.org/ns#> SELECT ?title ?price WHERE { ?x ns:price ?price . FILTER (?price < 30.5) ?x dc:title ?title . }");

    Response response = rapsql_rpc_res("/rapsql_s2c", payload);
    test_response_code(response);
  }

  @Test
  @Order(5)
  public void test_rapsql_r2c(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("graph_name", PREPARED_DATA);
    payload.put("lang_str", "TTL");
    payload.put("rdf_str", "@prefix dc: <http://purl.org/dc/elements/1.1/> . @prefix : <http://example.org/book/> . @prefix ns: <http://example.org/ns#> . :book1 dc:title 'SPARQL Tutorial' . :book1 ns:price 42 . :book2 dc:title 'The Semantic Web' . :book2 ns:price 23 .");

    Response response = rapsql_rpc_res("/rapsql_r2c", payload);
    test_response_code(response);
  }

  @Test
  @Order(6)
  public void test_rapsql_cypher(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("cypher_query", "SELECT * FROM ag_catalog.cypher('api-test-data', $$ MATCH (x)-[{uri:'http://example.org/ns#price'}]->(price),(x)-[{uri:'http://purl.org/dc/elements/1.1/title'}]->(title) WHERE price.value < 30.5 RETURN title.stringrep, price.stringrep $$) AS (title ag_catalog.agtype, price ag_catalog.agtype)");

    Response response = rapsql_rpc_res("/rapsql_cypher", payload);
    test_response_code(response);
  }

  @Test
  @Order(7)
  public void test_rapsql_sparql(TestInfo testInfo) {
    System.out.println("Running test: " + testInfo.getDisplayName());
    JsonObject payload = new JsonObject();
    payload.put("graph_name", PREPARED_DATA);
    payload.put("sparql_query", "PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX ns: <http://example.org/ns#> SELECT ?title ?price WHERE{ ?x ns:price ?price . FILTER (?price < 30.5) ?x dc:title ?title . }");

    Response response = rapsql_rpc_res("/rapsql_sparql", payload);
    test_response_code(response);
  }
}

