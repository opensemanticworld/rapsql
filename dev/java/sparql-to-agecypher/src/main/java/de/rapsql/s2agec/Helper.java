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
import org.apache.jena.riot.RDFDataMgr;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Helper {

  // ! in development for generic rdf data import of API
  // TODO: change to json return type of API rapsql_rdf()
  // public static Void print_rapsql_RDFtoCypher(String graph_name) {
  //   // https://jena.apache.org/documentation/io/rdf-input.html#using-rdfdatamgr
  //   // start testing the postgres api for different input formats
  //   try {
  //     String language = "TTL"; 
  //     String rdf_input_str = "<http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> \"SPARQL Tutorial\" .";
  //     String rdf_to_cypher = PostgresApi.rapsql_rdf(graph_name, language, rdf_input_str);
  //     System.out.println("----------------- RDF TO CYPHER -----------------\n"+ rdf_to_cypher + "\n");
  //   } catch (Exception e) {
  //     System.out.println(e);
  //   }
  //   return null;
  // }
  
  public static Void print_transpiler_tests(String graph_name) {
    // file access boilerplate described in 
    // TranspilerTest.java, MethodProvider()
    try {
      File f = new File("src/test/resources/ttl-sparql");
      FileFilter directoryFilter = new FileFilter() {
        public boolean accept(File file) {
          return file.isDirectory();
        }
      };
      File[] files = f.listFiles(directoryFilter);
      Arrays.sort(files); // sort files
      for (File folder_ : files) {
        String divider = StringUtils.repeat("%", 100);
        System.out.println("\n\n\n" + divider);
        String folder = folder_.getCanonicalPath();
        // System.out.println(folder);

        Path rdf_path = Paths.get(folder, "rdf.ttl");
        System.out.println("\n----------------- RDF PATH -----------------\n"+ rdf_path + "\n");
        
        // Create model from ttl
        Model rdf_model = RDFDataMgr.loadModel(rdf_path.toString());
        System.out.println("----------------- RDF MODEL -----------------\n"+ rdf_model + "\n");
        String rdf_to_cypher = RdfToCypher.rdfToCypher(graph_name, rdf_model);
        System.out.println("----------------- RDF TO CYPHER -----------------\n"+ rdf_to_cypher + "\n");

        // Iterate over all sparql files in queries folder
        FileFilter sparqlFilter = new FileFilter() {
          public boolean accept(File file) {
            String extension = "";

            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i+1);
            }
            return file.isFile() && (extension.equals("sparql"));
          }
        };
        File[] query_files = Paths.get(folder, "queries").toFile().listFiles(sparqlFilter);
        Arrays.sort(query_files); // sort query files
        for(File query_file: query_files) {
          try {
            System.out.println("----------------- SPARQL PATH -----------------\n"+ query_file + "\n");
            String sparql_query = null;
            sparql_query = new String(Files.readAllBytes(query_file.toPath()));
            System.out.println("----------------- SPARQL QUERY -----------------\n"+ sparql_query);
            System.out.println("\n----------------- SPARQL TO CYPHER -----------------\n"+ SparqlToCypher.rapsql_s2c(graph_name, sparql_query) + "\n\n\n");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  
}
