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

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeVisitor;
import org.apache.jena.graph.Node_ANY;
import org.apache.jena.graph.Node_Blank;
import org.apache.jena.graph.Node_Graph;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLateral;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;

public class SparqlAlgebraVisit implements OpVisitor {
  // mapping variables
  private String cypher;
  private Map<Var, String> Sparql_to_cypher_variable_map;
  private Map<String, Object> Cypher_to_sparql_variable_map;
  private int blank_node_num = 0;
  private Map<Node_Blank, Var> Sparql_blank_node_to_var_map;
  private boolean isQueryConversionSuccesful = true;
  private String conversionErrors = "";
  
  // initialize instance
  public SparqlAlgebraVisit() {
    cypher = new String();
    Sparql_blank_node_to_var_map = new HashMap<Node_Blank, Var>();
    Sparql_to_cypher_variable_map = new HashMap<Var, String>();
    Cypher_to_sparql_variable_map = new HashMap<String, Object>();
  }
  
  protected String create_or_get_variable(Node_Blank it) {
    String var_name = "blankvar" + (blank_node_num++);
    Var variable = Var.alloc(var_name + it.getBlankNodeId().toString());
    Sparql_blank_node_to_var_map.put(it, variable);
    String created_var = create_or_get_variable(variable);
    return created_var;
  }

  protected String create_or_get_variable(Var allocated_var) {
    // TODO Account for variable names to ensure that there is no collision, and the created variable is valid in Cypher conventions
    // https://neo4j.com/docs/cypher-manual/current/syntax/naming/
    // https://www.w3.org/TR/sparql11-query/#rVARNAME
    if(Sparql_to_cypher_variable_map.containsKey(allocated_var)) return Sparql_to_cypher_variable_map.get(allocated_var);
    else {
      Sparql_to_cypher_variable_map.put(allocated_var, allocated_var.getName());
      Cypher_to_sparql_variable_map.put(allocated_var.getName(), allocated_var);
      return Sparql_to_cypher_variable_map.get(allocated_var);
    }
  }

  @Override // MATCH
  public void visit(OpBGP opBGP) {
    // TODO: Instead of handling opTriple in this function, handle it in its visitor

    //! ONLY FOR DEBUG VISITOR ! COMMENT OUT FOR STACK INTEGRATION !
    // System.out.println("\nIn opBGP\n" + opBGP.toString()+"\n");
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    java.util.Iterator<Triple> it = opBGP.getPattern().iterator();
    cypher = cypher + "MATCH ";
    while(it.hasNext()) {
      Triple t = it.next();
      // CreateCypher visitor = new CreateCypher();
      NodeVisitor cypherNodeMatcher = new NodeVisitor() {
        @Override
        public String visitBlank(Node_Blank it, BlankNodeId id) {
          return create_or_get_variable(it);
        }

        @Override
        public String visitLiteral(Node_Literal it, LiteralLabel lit) {
          return 
            (lit.language().equals("")) ?
              (String.format("{uri:\'\', typeiri:\'%s\', lexform:\'%s\'}",
                  lit.getDatatypeURI(), lit.getLexicalForm()))
            : 
              (String.format("{uri:\'\', typeiri:\'%s\', lexform:\'%s\', langtag:\'%s\'}",
                  lit.getDatatypeURI(), lit.getLexicalForm(), lit.language()));
        }

        @Override
        public String visitURI(Node_URI it, String uri) {
          return String.format("{uri:\'%s\'}", uri);
        }

        @Override
        public String visitVariable(Node_Variable it, String name) {
          return create_or_get_variable(Var.alloc(it));
        }

        @Override
        public Object visitTriple(Node_Triple it, Triple triple) {
          return null;
        }

        @Override
        public Object visitGraph(Node_Graph it, Graph graph) { 
          return null;
        }
        
        @Override
        public String visitAny(Node_ANY it) {
          return null;
        } 
      };
      
      cypher = cypher + "(" 
          + t.getMatchSubject().visitWith(cypherNodeMatcher) 
          + ")-[" 
          + t.getMatchPredicate().visitWith(cypherNodeMatcher) 
          + "]->("
          + t.getMatchObject().visitWith(cypherNodeMatcher)
          + "),";
    }
    cypher = cypher.substring(0, cypher.length() - 1);
    cypher = cypher.concat(" ");
  }

  @Override // WHERE 
  public void visit(OpFilter opFilter) {

    //! ONLY FOR DEBUG VISITOR ! COMMENT OUT FOR STACK INTEGRATION !
    // System.out.println("\nIn opFilter\n" + opFilter.toString());
    // System.out.println("\nIn opFilter.getSubOp()\n" + opFilter.getSubOp());
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    opFilter.getSubOp().visit(this);  // pass on suboperations

    cypher = cypher.concat("WHERE "); // start WHERE statement

    // build WHERE clause depending on filter input expressions
    java.util.Iterator<Expr> it = opFilter.getExprs().iterator();
    // take care of nested expressions
    while (it.hasNext()) {
      Expr expr = it.next();
      // build clause for every variable in expression list
      for (Var exprVar : expr.getVarsMentioned()) {
        String operator = ((operator = expr.getFunction().getOpName()) != null) 
          ? operator // basic expression
          : expr.getFunction().getFunctionSymbol().getSymbol(); // advanced expression

        String arg1 = Sparql_to_cypher_variable_map.get(exprVar).toString();
        String arg2 = expr.getFunction().getArg(2).toString(); 
        if (operator != "regex") { // build clause by basic expr
          cypher = cypher.concat(arg1 + ".value " + operator + " " + arg2.replace("\"", "'"));
        } else { // build clause by regex 
          cypher = cypher.concat("toString(" + arg1 + ".value) " + operator.replace("regex", "=~") + " ");
          if (expr.getFunction().numArgs() == 3) { // check 3rd argument
            String rex3 = expr.getFunction().getArg(3).toString().replace("\"", "");
            if (rex3.equals("i")) { // case: i
              cypher = cypher.concat("'(?i)"); 
              cypher = cypher.concat(arg2.replace("\"", "") + "'");
            }
            // TODO take care of regex cases as 3rd expr argument (add all available)
            // TODO compare all sparql filter cases with cypher 
          } else {
            cypher = cypher.concat(arg2.replace("\"", "'"));
          }
        }
      }  
    }
  }
  
  @Override // RETURN
  public void visit(OpProject opProject) {
    //! ONLY FOR DEBUG VISITOR ! COMMENT OUT FOR STACK INTEGRATION !
    // System.out.println("\nIn opProject\n" + opProject.toString());
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    // provide sub statements to other visitors
    opProject.getSubOp().visit(this);

    // transform sparql FILTER into cypher RETURN clause
    cypher = cypher.concat(" RETURN ");
    for(Var var: opProject.getVars()) {
      // System.out.println("PROJECT\n"+ var);
      cypher = cypher.concat(Sparql_to_cypher_variable_map.get(var) + ".stringrep, ");
      // TODO: check type conversion
      // cypher = cypher.concat(Sparql_to_cypher_variable_map.get(var) + ".value, ");
    }
    cypher = cypher.substring(0, cypher.length() - 2);
    cypher = cypher.concat(" $$) AS (");
    for(Var var: opProject.getVars()) {
      cypher = cypher.concat(Sparql_to_cypher_variable_map.get(var) + " ag_catalog.agtype, ");
    }
    cypher = cypher.substring(0, cypher.length() - 2);
    cypher = cypher.concat(")");
    // TODO: create test cases for cypher-based term endings
    // cypher = cypher.concat(");"); 
  }

  //////////////////////// FUTURE WORK BELOW ////////////////////////
  // TODO: 1. integrate rdf2pg instead of custom graph schema
  // TODO: 2. compare concepts between RDF and PG in more detail, 
  // TODO: 3. add parametrized test cases from public benchmarks 
  // TODO: 4. implementation using provided bench tests exhaustively
  
  @Override
  public void visit(OpQuadPattern quadPattern){
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpQuadPattern\n";
  }

  @Override
  public void visit(OpQuadBlock quadBlock) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpQuadBlock\n";
  }

  @Override
  public void visit(OpTriple opTriple) {   
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpTriple\n";
  }

  @Override
  public void visit(OpQuad opQuad) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpQuad\n";
  }

  @Override
  public void visit(OpPath opPath) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpPath\n";
  }

  @Override
  public void visit(OpTable opTable) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpTable\n";
  }

  @Override
  public void visit(OpNull opNull) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpNull\n";
  }

  @Override
  public void visit(OpProcedure opProc) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpProcedure\n";
  }

  @Override
  public void visit(OpPropFunc opPropFunc) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpPropFunc\n";
  }

  @Override
  public void visit(OpGraph opGraph) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpGraph\n";
  }

  @Override
  public void visit(OpService opService) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpService\n";
  }

  @Override
  public void visit(OpDatasetNames dsNames) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpDatasetNames\n";
  }

  @Override
  public void visit(OpLabel opLabel) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpLabel\n";
  }

  @Override
  public void visit(OpAssign opAssign) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpAssign\n";
  }

  @Override
  public void visit(OpExtend opExtend) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpExtend\n";
  }

  @Override
  public void visit(OpJoin opJoin) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpJoin\n";
  }

  @Override
  public void visit(OpLeftJoin opLeftJoin) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpLeftJoin\n";
  }

  @Override
  public void visit(OpUnion opUnion) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpUnion\n";
  }

  @Override
  public void visit(OpDiff opDiff) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpDiff\n";
  }

  @Override
  public void visit(OpMinus opMinus) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpMinus\n";
  }

  @Override
  public void visit(OpConditional opCondition) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpCondition\n";
  }

  @Override
  public void visit(OpSequence opSequence) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpSequence\n";
  }

  @Override
  public void visit(OpDisjunction opDisjunction) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpDisjunction\n";
  }

  @Override
  public void visit(OpList opList) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpList\n";
  }

  @Override
  public void visit(OpOrder opOrder) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpOrder\n";
  }

  @Override
  public void visit(OpReduced opReduced) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpReduced\n";
  }

  @Override
  public void visit(OpDistinct opDistinct) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpDistinct\n";
  }

  @Override
  public void visit(OpSlice opSlice) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpSlice\n";
  }

  @Override
  public void visit(OpGroup opGroup) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpGroup\n";
  }

  @Override
  public void visit(OpTopN opTop) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpTop\n";
  }
  
  @Override
  public void visit(OpLateral opLateral) {
    this.isQueryConversionSuccesful = false;
    this.conversionErrors += "Unsupported Algebra type OpLateral\n";
  }
  //////////////////////// FUTURE WORK ABOVE ////////////////////////

  // provide cypher query or throw exception for unassigned patterns
  public String getCypher() throws QueryException {
    if(this.isQueryConversionSuccesful) return cypher;
    else throw new QueryException(this.conversionErrors);
  }
}