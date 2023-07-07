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

/////////// ! MAIN ONLY USED FOR DEBUGGING NEW IMPLEMENATIONS ! /////////

//// ! COMMENT ON IMPORT STATEMENTS FOR STACK INTEGRATION ! ////

// import java.util.Iterator;
// import org.apache.jena.atlas.json.JsonValue;

//// ! COMMENT ON IMPORT STATEMENTS FOR STACK INTEGRATION ! ////
public class Main {
  public static void main( String[] args )  { 
    //// ! COMMENT ON ALL MAIN CONTENT BELOW FOR STACK INTEGRATION ! ////
    try {
    //////////////  WORKING PATTERNS BELOW ///////////////////
    // perform and print transpiler tests
    Helper.print_transpiler_tests("rdf"); 
    //////////////  WORKING PATTERNS ABOVE ///////////////////    

    ////////////// ! IN DEVELOPMENT BELOW ! //////////////////
    // TODO: investigate permissions for input of any rdf datatype    
    // TODO: implement automated API tests as soon as rdf API works
    //       to get rid of user decision and to close the dev cycle!
    //       reminder: investigate JUnit rest api testing strategy
    ////////////// ! IN DEVELOPMENT ABOVE ! //////////////////
    } catch (Exception e) {
      System.err.println(e);
    }
    //// ! COMMENT ON ALL MAIN CONTENT ABOVE FOR STACK INTEGRATION ! ////
  }
}