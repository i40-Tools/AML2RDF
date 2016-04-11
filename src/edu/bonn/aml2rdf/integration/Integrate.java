/*
 * @class Integration.java
 * 
 * @Version =1.0
 *
 * @Date 4/11/2016
 * 
 * @Copyright EIS University of Bonn */

package edu.bonn.aml2rdf.integration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;

import edu.bonn.aml2rdf.GUI.RdfGUI;
import edu.bonn.aml2rdf.rdfconvertor.RdfConvertor;

/**
 * @author omar
 *
 */
public class Integrate {

	/*
	 * This method integrates the RDF files into single RDF , The default
	 * conversion is in in Turtle format. Integration is performed through
	 * SPARQL Query.
	 * 
	 */

	public void integrate_rdf() throws IOException {
		// TODO Auto-generated method stub

		// Sets output file of RDF integration
		FileWriter output = new FileWriter(new RdfConvertor().getpath() + "integration.ttl");
		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, getNewModel(), RDFFormat.TURTLE_BLOCKS);
		output.write(sw.toString());
		output.close();

	}

	/**
	 * creates a new RDF graph using Construct Query.
	 * 
	 * @return new model
	 */
	protected Model getNewModel() {

		// loads two RDF files in turtle format from output folder.
		Model modelY = FileManager.get().loadModel(new RdfConvertor().getpath() + RdfGUI.files_[0].getName() + ".ttl");
		Model modelX = FileManager.get().loadModel(new RdfConvertor().getpath() + RdfGUI.files_[1].getName() + ".ttl");

		// gives those two files a URI for querying data
		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(modelX);
		dataset.addNamedModel("http://example/named-1", modelX);
		dataset.addNamedModel("http://example/named-2", modelY);

		String queryString = null;

		// Reads the Query from file
		try (InputStream res = Integrate.class.getResourceAsStream("/M1.2.txt")) {
			queryString = IOUtils.toString(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Executes the Query and returns new model file
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
			Model resultModel = qexec.execConstruct();
			qexec.close();
			return resultModel;

		}

	}

}
