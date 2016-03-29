package test.sparql;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
public class Main {
	static String abc="Query Result:";

	public static String integrate_double() {
		// TODO Auto-generated method stub
		//	OutputStream f=new FileOutputStream("c:/data.rdf");
		
	 	//	FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
	 	//	Model model=FileManager.get().loadModel("c:/Users/omar/Documents/workspace-sts-3.7.0.RELEASE/IntegrationTool/src/data.rdf");
	      //  model.write(f,"Turtle");
	 		 
			Model modelY=FileManager.get().loadModel(Start.file.getAbsolutePath());		  
			Model modelX=FileManager.get().loadModel(Start.file2.getAbsolutePath());		  

			
			Dataset dataset = DatasetFactory.create() ;
			dataset.setDefaultModel(modelX) ;
			dataset.addNamedModel("http://example/named-1", modelX) ;
			dataset.addNamedModel("http://example/named-2", modelY) ;
			 
					
			String queryString =
					 "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
	                 "prefix aml:<http://vocab.cs.uni-bonn.de/aml#>"
	                 + "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "+
	                 "prefix schema:<http://schema.org/> "+
	                 "prefix xsd:<http://www.w3.org/2001/XMLSchema#>"+
			" Select  distinct ?double "+
	                 "from named<http://example/named-1>"+
			         "from named<http://example/named-2>"+
	                 
			 "where {{ graph <http://example/named-1>{{?x aml:hasValue ?y}"
			 + "Bind(xsd:double(?y) as ?double)}} "
			 + "UNION"
			 + "{ graph <http://example/named-2>{{?x aml:hasValue ?y}"
			 + "Bind(xsd:double(?y) as ?double)}}"
			 + "} ";
			  Query query = QueryFactory.create(queryString) ;
			  try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
			    ResultSet results = qexec.execSelect() ;
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
	//		      RDFNode x = soln.get("z") ;       // Get a result variable by name.
	//		      Resource r = soln.getResource("xa") ; // Get a result variable - must be a resource
			      Literal l = soln.getLiteral("double") ;   // Get a result variable - must be a literal
                  abc=abc+"\n"+l; 			      
            
			    }
			  qexec.close();
			  }
return abc;
	}

}
