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

// import java.nio.file.Path;

// import java.io.File;
// import java.nio.file.Files;
// import java.io.FileFilter;


/////////// ! MAIN ONLY USED FOR DEBUGGING NEW IMPLEMENATIONS ! /////////

//// ! COMMENT ON IMPORT STATEMENTS FOR STACK INTEGRATION ! ////

// import java.util.Iterator;
// import org.apache.jena.atlas.json.JsonValue;

//// ! COMMENT ON IMPORT STATEMENTS FOR STACK INTEGRATION ! ////
public class Main {
  public static void main( String[] args )  { 
    //// ! COMMENT ON ALL MAIN CONTENT BELOW FOR STACK INTEGRATION ! ////
    // try {
    // //////////////  WORKING PATTERNS BELOW ///////////////////

    // // // -- simple database mapping (sdm) -- //
    // // String graph_sdm_sp2b100 = "sdm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_sdm_sp2b100);
    // // RdfGraph.age_create_graph(graph_sdm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_sdm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/sp2b100/sdm-instance.ypg")
    // // );
    // // String graph_sdm_w3c5 = "sdm_w3c5";
    // // RdfGraph.age_drop_graph(graph_sdm_w3c5);
    // // RdfGraph.age_create_graph(graph_sdm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_sdm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/w3c5/sdm-instance.ypg")
    // // );


    // // // // -- generic database mapping (gdm) -- //
    // // String graph_gdm_sp2b1k = "gdm_sp2b1k";
    // // RdfGraph.age_drop_graph(graph_gdm_sp2b1k);
    // // RdfGraph.age_create_graph(graph_gdm_sp2b1k);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdm_sp2b1k, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/sp2b1k/gdm-instance.ypg")
    // // );
    // String graph_gdm_sp2b1M = "gdm_sp2b1M";
    // // RdfGraph.age_drop_graph(graph_gdm_sp2b1M);
    // RdfGraph.age_create_graph(graph_gdm_sp2b1M);
    // RdfGraph.age_import_ypg(
    //   graph_gdm_sp2b1M, 
    //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/sp2b1M/gdm-instance.ypg")
    // );

    // // String graph_gdm_sp2b100 = "gdm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_gdm_sp2b100);
    // // RdfGraph.age_create_graph(graph_gdm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/sp2b100/gdm-instance.ypg")
    // // );
    // // String graph_gdm_w3c5 = "gdm_w3c5";
    // // RdfGraph.age_drop_graph(graph_gdm_w3c5);
    // // RdfGraph.age_create_graph(graph_gdm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/w3c5/gdm-instance.ypg")
    // // );

    // // // // -- complete database mapping (cdm) -- //
    // // String graph_cdm_sp2b100 = "cdm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_cdm_sp2b100);
    // // RdfGraph.age_create_graph(graph_cdm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_cdm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/sp2b100/cdm-instance.ypg")
    // // );
    // // String graph_cdm_w3c5 = "cdm_w3c5";
    // // RdfGraph.age_drop_graph(graph_cdm_w3c5);
    // // RdfGraph.age_create_graph(graph_cdm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_cdm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/w3c5/cdm-instance.ypg")
    // // );


















    // // ------------------- RDF IMPORTS ------------------- //

    // // // -- subject predicate object merge (spom) "custom base model" -- //
    // // String graph_spom_sp2b100 = "spom_sp2b100";
    // // String rdf_sp2b_100_n3_raw = "src/test/resources/sp2b/100/rdf.n3";
    // // RdfGraph.age_drop_graph(graph_spom_sp2b100);
    // // RdfGraph.age_create_graph(graph_spom_sp2b100);
    // // RdfGraph.age_import_rdf(graph_spom_sp2b100, rdf_sp2b_100_n3_raw);

    // // String graph_spom_w3c5 = "spom_w3c5";
    // // String rdf_w3c_5_n3_raw = "src/test/resources/ttl-sparql/w3c_test5/rdf.ttl";
    // // RdfGraph.age_drop_graph(graph_spom_w3c5);
    // // RdfGraph.age_create_graph(graph_spom_w3c5);
    // // RdfGraph.age_import_rdf(graph_spom_w3c5, rdf_w3c_5_n3_raw);

    // // // -- simple database mapping (sdm) -- //
    // // String graph_sdm_sp2b100 = "sdm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_sdm_sp2b100);
    // // RdfGraph.age_create_graph(graph_sdm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_sdm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/sdm-instance-sp2b-100.ypg")
    // // );
    // // String graph_sdm_w3c5 = "sdm_w3c5";
    // // RdfGraph.age_drop_graph(graph_sdm_w3c5);
    // // RdfGraph.age_create_graph(graph_sdm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_sdm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/sdm-instance-w3c-5.ypg")
    // // );

    // // // -- simple database mapping merge (sdmm) -- //
    // // String graph_sdmm_sp2b100 = "sdmm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_sdmm_sp2b100);
    // // RdfGraph.age_create_graph(graph_sdmm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_sdmm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/sdmm-instance-sp2b-100.ypg")
    // // );
    // // String graph_sdmm_w3c5 = "sdmm_w3c5";
    // // RdfGraph.age_drop_graph(graph_sdmm_w3c5);
    // // RdfGraph.age_create_graph(graph_sdmm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_sdmm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/sdmm-instance-w3c-5.ypg")
    // // );

    // // // -- generic database mapping (gdm) -- //
    // // String graph_gdm_sp2b100 = "gdm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_gdm_sp2b100);
    // // RdfGraph.age_create_graph(graph_gdm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/gdm-instance-sp2b-100.ypg")
    // // );
    // // String graph_gdm_w3c5 = "gdm_w3c5";
    // // RdfGraph.age_drop_graph(graph_gdm_w3c5);
    // // RdfGraph.age_create_graph(graph_gdm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/gdm-instance-w3c-5.ypg")
    // // );

    // // // -- generic database mapping merge (gdmm) -- //
    // // String graph_gdmm_sp2b100 = "gdmm2_sp2b100";
    // // RdfGraph.age_drop_graph(graph_gdmm_sp2b100);
    // // RdfGraph.age_create_graph(graph_gdmm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdmm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/gdmm-instance-sp2b-100.ypg")
    // // );
    // // String graph_gdmm_w3c5 = "gdmm_w3c5";
    // // RdfGraph.age_drop_graph(graph_gdmm_w3c5);
    // // RdfGraph.age_create_graph(graph_gdmm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_gdmm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/gdmm-instance-w3c-5.ypg")
    // // );
    // // String graph_gdmm_w3c5 = "gdmm2_w3c5";
    // // RdfGraph.age_drop_graph(graph_gdmm_w3c5);
    // // RdfGraph.age_create_graph(graph_gdmm_w3c5);
    // // RdfGraph.age_import2_ypg(
    // //   graph_gdmm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/gdmm-instance-w3c-5.ypg")
    // // );

    // // // -- complete database mapping (cdm) -- //
    // // String graph_cdm_sp2b100 = "cdm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_cdm_sp2b100);
    // // RdfGraph.age_create_graph(graph_cdm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_cdm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/cdm-instance-sp2b-100.ypg")
    // // );
    // // String graph_cdm_w3c5 = "cdm_w3c5";
    // // RdfGraph.age_drop_graph(graph_cdm_w3c5);
    // // RdfGraph.age_create_graph(graph_cdm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_cdm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/cdm-instance-w3c-5.ypg")
    // // );

    // // // -- complete database mapping merge (cdmm) -- //
    // // String graph_cdmm_sp2b100 = "cdmm_sp2b100";
    // // RdfGraph.age_drop_graph(graph_cdmm_sp2b100);
    // // RdfGraph.age_create_graph(graph_cdmm_sp2b100);
    // // RdfGraph.age_import_ypg(
    // //   graph_cdmm_sp2b100, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/cdmm-instance-sp2b-100.ypg")
    // // );
    // // String graph_cdmm_w3c5 = "cdmm_w3c5";
    // // RdfGraph.age_drop_graph(graph_cdmm_w3c5);
    // // RdfGraph.age_create_graph(graph_cdmm_w3c5);
    // // RdfGraph.age_import_ypg(
    // //   graph_cdmm_w3c5, 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg/cdmm-instance-w3c-5.ypg")
    // // );

    // // String graph_gdmr_w3c5 = "gdmr_w3c5";
    // // RdfGraph.age_drop_graph(graph_gdmr_w3c5);
    // // RdfGraph.age_create_graph(graph_gdmr_w3c5 );
    // // RdfGraph.age_import_ypg(
    // //   graph_gdmr_w3c5 , 
    // //   Path.of("/usr/local/docker/rapsql/data/rdf2pg-rapsql/gdmr-instance-w3c-5.ypg")
    // // );

    // // // definitions for inspection //
    
    // // String query_file_path = "src/test/resources/sp2b/100/queries/q4.sparql";
    // // String rdf_file_path = "src/test/resources/notest/examples/distinct/rdf.ttl";
    // // String query_file_path = "src/test/resources/notest/examples/distinct/queries/q1.sparql";
    // // String graph_name = "distinct";
    // // String sparql_query = RdfGraph.get_sparql(query_file_path);
    // // String cypher_query = SparqlToCypher.rapsql_s2c(graph_name, sparql_query) + ";";
    // // //////////////////////// FIRST EXTRACT RUNNING QUERY BEFORE ANY CHANGES ////////////////////////
    // // String cypher_query = "SELECT * FROM ag_catalog.cypher('rdf-graph', $$ MATCH (article1)-[{uri:'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'}]->({uri:'http://localhost/vocabulary/bench/Article'}),(article2)-[{uri:'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'}]->({uri:'http://localhost/vocabulary/bench/Article'}),(article1)-[{uri:'http://purl.org/dc/elements/1.1/creator'}]->(author1),(author1)-[{uri:'http://xmlns.com/foaf/0.1/name'}]->(name1),(article2)-[{uri:'http://purl.org/dc/elements/1.1/creator'}]->(author2),(author2)-[{uri:'http://xmlns.com/foaf/0.1/name'}]->(name2),(article1)-[{uri:'http://swrc.ontoware.org/ontology#journal'}]->(journal),(article2)-[{uri:'http://swrc.ontoware.org/ontology#journal'}]->(journal) RETURN name1.stringrep, name2.stringrep $$) AS (name1 ag_catalog.agtype, name2 ag_catalog.agtype);";
    // // System.out.println("rapsql_s2c\n" + cypher_query + "\n");

    // // // interact with rapsql database //
    // // RdfGraph.age_drop_graph(graph_name);
    // // RdfGraph.age_create_graph(graph_name);
    // // RdfGraph.age_import_rdf(graph_name, rdf_file_path);
    // // System.out.println("AGE Cypher Execution Result\n" + RdfGraph.exec_query(cypher_query) + "\n");

    // // // perform and print transpiler tests //
    // // String graph_name = "test";
    // // Helper.print_transpiler_tests(graph_name); 
    // // String graph_name="sp2b_100_custom";
    // // Helper.print_sp2b_tests(graph_name);



    // //////////////  WORKING PATTERNS ABOVE ///////////////////    


    // ////////////// ! IN DEVELOPMENT BELOW ! //////////////////

    // // TODO: investigate permissions for input of any rdf datatype    
    
    // ////////////// ! IN DEVELOPMENT ABOVE ! //////////////////
    // } catch (Exception e) {
    //   System.err.println(e);
    // }
    //// ! COMMENT ON ALL MAIN CONTENT ABOVE FOR STACK INTEGRATION ! ////
  }
}