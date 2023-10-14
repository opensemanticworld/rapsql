// package de.rapsql.s2agec;

// import java.nio.file.Path;
// import java.nio.file.Files;
// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.ResultSet;
// import java.sql.ResultSetMetaData;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;

// import org.apache.jena.rdf.model.Model;
// import org.apache.jena.riot.RDFDataMgr;

// // import org.apache.commons.lang3.StringUtils;

// public class Backup {
//   // private static final String DB_URL = "jdbc:postgresql://sysarch.digital.isc.fraunhofer.de:5444/postgres";
//   private static final String DB_URL = "jdbc:postgresql://localhost:5432/rapsql";
//   private static final String USER = "postgres";
//   private static final String PASS = "postgres";

//   // create a graph in AGE
//   public static void age_create_graph(String graph_name) throws SQLException {
//     try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS) ) {
//       Statement stmt = conn.createStatement(); 
//       stmt.execute("LOAD 'age';");
//       stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
//       stmt.execute("SELECT * FROM ag_catalog.create_graph('" + graph_name  + "');");
//       System.out.println("-------- AGE GRAPH '" + graph_name + "' CREATED --------");

//     } catch (SQLException e) { e.printStackTrace(); }
//   }

//   // drop a graph in AGE
//   public static void age_drop_graph(String graph_name) throws SQLException {
//     try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS) ) {
//       Statement stmt = conn.createStatement(); 
//       stmt.execute("LOAD 'age'");
//       stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
//       stmt.execute("SELECT * FROM drop_graph('" + graph_name  + "', true);");
//       System.out.println("-------- AGE GRAPH '" + graph_name + "' DELETED --------");
//     } catch (SQLException e) { e.printStackTrace(); }    
//   }

//   // import rdf data into an existing graph in AGE
//   public static void age_import_rdf(String graph_name, String rdf_file_path) throws SQLException {
//     long start_total = System.currentTimeMillis();
//     long start, end;
//     Path rdf_path = Path.of(rdf_file_path);

//     // load the rdf model and print the execution time
//     start = System.currentTimeMillis();
//     Model rdf_model = RDFDataMgr.loadModel(rdf_path.toString());
//     end = System.currentTimeMillis();
//     System.out.println("-------- RDF LOAD TIME: " + (end - start) + "ms --------");

//     // convert rdf model to cypher data mapping and print the execution time
//     start = System.currentTimeMillis();
//     String rdf_to_cypher = RdfToCypher.rdfToCypher(graph_name, rdf_model);
//     end = System.currentTimeMillis();
//     System.out.println("-------- RDF TO CYPHER TIME: " + (end - start) + "ms --------");
    
//     // execute rdf_to_cypher statement and return the query time
//     start = System.currentTimeMillis();
//     try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS) ) {
//     Statement stmt = conn.createStatement(); 
//     stmt.execute("LOAD 'age';");
//     stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
//     stmt.execute(rdf_to_cypher); // rdf to cypher data mapping here
//     end = System.currentTimeMillis();
//     System.out.println("-------- AGE GRAPH '" + graph_name + "' RDF IMPORT SUCCESS --------");
//     System.out.println("-------- DB IMPORT TIME: " + (end - start) + "ms --------");

//     // print total import time
//     long end_total = System.currentTimeMillis();
//     System.out.println("-------- TOTAL IMPORT TIME: " + (end_total - start_total) + "ms --------");
//     } catch (SQLException e) { e.printStackTrace(); }    
//   }

//   // provide sparql query from file
//   public static String get_sparql(String query_file_path) throws IOException {
//     // FileFilter sparqlFilter = new FileFilter() {
//     //   public boolean accept(File file) {
//     //     String extension = "";
//     //     int i = file.getName().lastIndexOf('.');
//     //     if (i > 0) { extension = file.getName().substring(i+1); }
//     //     return file.isFile() && (extension.equals("sparql"));
//     //   }
//     // };
//     // File[] query_files = Paths.get(query_folder_path, "queries").toFile().listFiles(sparqlFilter);
//     // Arrays.sort(query_files); // sort .sparql query files
//     Path query_path = Path.of(query_file_path);
//     String sparql_query = "";
//     try {
//       sparql_query = new String(Files.readAllBytes(query_path));
//     } catch (IOException e) { e.printStackTrace(); }
//     return sparql_query;
//   }

//   // execute query, return result set if available
//   public static String exec_query(String query) throws SQLException {
//     Set<Map<String, String>> query_res_map = new HashSet<Map<String, String>>();
//     try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS) ) {
//       Statement stmt = conn.createStatement(); 
//       stmt.execute("LOAD 'age'");
//       stmt.execute("SET search_path = ag_catalog, \"$user\", public;");             
//       ResultSet rs = stmt.executeQuery(query); // sparql-to-cypher result here
//       ResultSetMetaData rsmd = rs.getMetaData();
//       int columnsNumber = rsmd.getColumnCount();
//       // create map of single cypher statements 
//       while (rs.next()) {
//         Map<String, String> res = new HashMap<String, String>();
//         for (int i = 1; i <= columnsNumber; i++) {
//           res.put(rsmd.getColumnName(i), rs.getString(i).replace("\"", ""));
//         }
//         query_res_map.add(res);
//       }
//     } catch (SQLException e) { e.printStackTrace(); }
//     return query_res_map.toString();    
//   }

//   // import ypg data into an existing graph in AGE
//   public static void age_import_ypg(String graph_name, Path ypg_cypher) throws SQLException {
//     long start_total = System.currentTimeMillis();
//     long start, end;
//     String age_cypher = "";
//     String _ypg_cypher = "";

//     // load the ypg cypher file 
//     try {
//       _ypg_cypher = new String(Files.readAllBytes(ypg_cypher));
//     } catch (IOException e) { e.printStackTrace(); }

//     // execute _ypg_cypher statement and return the query time
//     start = System.currentTimeMillis();
//     try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS) ) {
//       Statement stmt = conn.createStatement(); 
//       stmt.execute("LOAD 'age';");
//       stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
//       // execute age cypher for each cypher statement row in ypg file using regex
//       for (String cypher : _ypg_cypher.split("\n")) { 
//         age_cypher =  "SELECT * FROM ag_catalog.cypher('" 
//                   + graph_name + "', $$ " 
//                   + cypher + " $$) AS (cypher ag_catalog.agtype);";
//         stmt.execute(age_cypher); // rdf to cypher data mapping here
//         // System.out.println("-------- AGE CYPER QUERY --------\n" + age_cypher + "\n");
//       }
//       end = System.currentTimeMillis();
//       System.out.println("-------- AGE GRAPH '" + graph_name + "' YPG IMPORT SUCCESS --------");
//       System.out.println("-------- DB IMPORT TIME: " + (end - start) + "ms --------");

//       // print total import time
//       long end_total = System.currentTimeMillis();
//       System.out.println("-------- TOTAL IMPORT TIME: " + (end_total - start_total) + "ms --------");
//     } catch (SQLException e) { e.printStackTrace(); }
//   }


//   // import ypg data into an existing graph in AGE using chunked cypher statements
//   public static void age_import_ypg_chunked(String graph_name, Path ypg_cypher) throws SQLException {
//     long start_total = System.currentTimeMillis();
//     long start, end;
//     String age_cypher = "";
//     String _ypg_cypher = "";
//     String[] _ypg_cypher_chunks = new String[1000]; 
//     int limit = 50; // set the limit to 50
//     String[] cypherChunks = _ypg_cypher.split("\n", limit);
//     for (String cypher : cypherChunks) {
//       System.out.println(cypher);
//     }
//   }










//   // import ypg data into an existing graph in AGE
//   public static void age_import2_ypg(String graph_name, Path ypg_cypher) throws SQLException {
//     long start_total = System.currentTimeMillis();
//     long start, end;
//     String age_cypher = "";
//     String _ypg_cypher = "";

//     // load the ypg cypher file 
//     try {
//       _ypg_cypher = new String(Files.readAllBytes(ypg_cypher));
//     } catch (IOException e) { e.printStackTrace(); }

//     // execute _ypg_cypher statement and return the query time
//     start = System.currentTimeMillis();
//     try ( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS) ) {
//       Statement stmt = conn.createStatement(); 
//       stmt.execute("LOAD 'age';");
//       stmt.execute("SET search_path = ag_catalog, \"$user\", public;");
//       // execute age cypher for each cypher statement row in ypg file using regex
//       age_cypher =  "SELECT * FROM ag_catalog.cypher('" 
//                 + graph_name + "', $$ " 
//                 + _ypg_cypher + " $$) AS (cypher ag_catalog.agtype);";
//       stmt.execute(age_cypher); // rdf to cypher data mapping here
//         // System.out.println("-------- AGE CYPER QUERY --------\n" + age_cypher + "\n");
//       end = System.currentTimeMillis();
//       System.out.println("-------- AGE GRAPH '" + graph_name + "' YPG IMPORT SUCCESS --------");
//       System.out.println("-------- DB IMPORT TIME: " + (end - start) + "ms --------");

//       // print total import time
//       long end_total = System.currentTimeMillis();
//       System.out.println("-------- TOTAL IMPORT TIME: " + (end_total - start_total) + "ms --------");
//     } catch (SQLException e) { e.printStackTrace(); }

//   }
// }
// package de.rapsql.s2agec;

// public class backup {
  
// }
