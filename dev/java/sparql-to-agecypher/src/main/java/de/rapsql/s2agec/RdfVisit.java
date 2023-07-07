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

import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;

class RdfVisit implements RDFVisitor {
  String operation;
  String label;

  public RdfVisit() { this.label = ""; this.operation = ""; }

  public RdfVisit(String operation) {
    this.label = ""; this.operation = operation + " ";
  }

  public void set_label(String label) { this.label = label; }

  @Override
  public String visitBlank(Resource r, AnonId id) {
    return String.format(
        "%s(%s {uri: '', id: '%s', stringrep: '%s', value: '%s'})",
        this.operation, this.label, id.getBlankNodeId().toString(),
        id.getBlankNodeId().toString(), id.getBlankNodeId().toString()
    );
  }

  @Override
  public String visitURI(Resource r, String uri) {
    return String.format(
        "%s(%s {uri: '%s', stringrep: '%s', value: '%s'})",
        this.operation,	this.label,	uri, uri,	uri
    );
  }

  @Override
  public String visitLiteral(Literal l) {
    // started using type checking for sparql filter "value"
    // (!integrate rdf2pg )

    return // cover the unusual case of "foo"^^rdf:langString
    (l.getDatatype() instanceof org.apache.jena.datatypes.xsd.impl.RDFLangString) ?
      (String.format("%s(%s {uri: '', typeiri: '%s', lexform: '%s', langtag: '%s', stringrep: '%s', value: %s})",
        this.operation,	this.label,
        l.getDatatypeURI(), l.getLexicalForm(),	l.getLanguage(),
        l.getLexicalForm() + "@" + l.getLanguage(),
      // started woriking on type checking here to provide FILTER queries
        ((l.getValue().getClass().getSimpleName().equals("Integer")) 
          || (l.getValue().getClass().getSimpleName().equals("BigDecimal"))
          ? l.getValue() : "'" +l.getValue() + "'")
      )) : (String.format(
        "%s(%s {uri: '', typeiri: '%s', lexform: '%s', stringrep: '%s', value: %s})",
        this.operation, this.label, l.getDatatypeURI(), l.getLexicalForm(),
        (l.getLexicalForm() + 
          // check literal type, string or other type
          (l.getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string") ?
            "" : "^^" + l.getDatatypeURI())),
        // property value stronger typed as number for internal expr or string
        ((l.getValue().getClass().getSimpleName().equals("Integer")) 
          || (l.getValue().getClass().getSimpleName().equals("BigDecimal"))
          // add quotation for strings
          ? l.getValue() : "'" +l.getValue() + "'")
      ));
  }
}