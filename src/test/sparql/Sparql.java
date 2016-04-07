package test.sparql;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;

import test.gui.SparqlGui;

public class Sparql {

	public static void integrate_double() throws IOException {
		// TODO Auto-generated method stub

		FileWriter output = null;
		output = new FileWriter(SparqlGui.file3.getAbsolutePath());
		StringWriter sw = new StringWriter();

		RDFDataMgr.write(sw, getNewModel(), RDFFormat.TURTLE_BLOCKS);
		output.write(sw.toString());
		output.close();

		// testQuery(getNewModel());

	}

	static Model getNewModel() {
		Model modelY = FileManager.get().loadModel(
				SparqlGui.file.getAbsolutePath());
		Model modelX = FileManager.get().loadModel(
				SparqlGui.file2.getAbsolutePath());

		// gives those two files a URI for querying data
		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(modelX);
		dataset.addNamedModel("http://example/named-1", modelX);
		dataset.addNamedModel("http://example/named-2", modelY);

		// Using construct to create the new model
		// converts it to integer
		String queryString = "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix aml:<http://vocab.cs.uni-bonn.de/aml#>"
				+ "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
				+ "prefix schema:<http://schema.org/> "
				+ "prefix xsd:<http://www.w3.org/2001/XMLSchema#>"
				+ "CONSTRUCT {  ?x ?y ?z ."
				+ "?x aml:hasValue ?doubleVal}"
				+ "from named <http://example/named-1>"
				+ "from named <http://example/named-2>"
				+ "where {{ graph <http://example/named-1>{"
				+ "{?x ?y ?z}}}"
				+ "UNION"
				+ "{ graph <http://example/named-2>{"
				+ "{?x ?y ?z}"
				+ "Optional{?x aml:hasValue ?value}"
				+ "Bind(xsd:double(STRBEFORE(str(?value), '.')) as ?doubleVal)}} "
				+ "FILTER (?y != aml:hasValue)" + "} ";

		Query query = QueryFactory.create(queryString);

		try (QueryExecution qexec = QueryExecutionFactory
				.create(query, dataset)) {
			Model resultModel = qexec.execConstruct();
			qexec.close();
			return resultModel;

		}

	}

	// performs the Query test on new model
	static ResultSet testQuery(Model model) {
		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(model);
		dataset.addNamedModel("http://example/named-3", model);

		String queryString = "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix aml:<http://vocab.cs.uni-bonn.de/aml#>"
				+ "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
				+ "prefix schema:<http://schema.org/> "
				+ "prefix xsd:<http://www.w3.org/2001/XMLSchema#>"
				+ " Select  * "
				+ "from named<http://example/named-3>"
				+ "where {?z ?xa ?double}";

		Query query = QueryFactory.create(queryString);

		try (QueryExecution qexec = QueryExecutionFactory
				.create(query, dataset)) {
			ResultSet results = qexec.execSelect();
			results = ResultSetFactory.copyResults(results);
			qexec.close();

			RDFDataMgr.write(System.out, model, RDFFormat.TURTLE_BLOCKS);

			return results; // Passes the result set out of the try-resources
		}

	}

}
