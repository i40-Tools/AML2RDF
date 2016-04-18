/*
 * @class RdfConvertor.java
 * 
 * @Version =1.0
 *
 * @Date 4/11/2016
 * 
 * @Copyright EIS University of Bonn */

package edu.bonn.aml2rdf.rdfconvertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;

import edu.bonn.aml2rdf.GUI.RdfGUI;
import edu.isi.karma.kr2rml.URIFormatter;
import edu.isi.karma.kr2rml.mapping.R2RMLMappingIdentifier;
import edu.isi.karma.kr2rml.writer.N3KR2RMLRDFWriter;
import edu.isi.karma.rdf.GenericRDFGenerator;
import edu.isi.karma.rdf.GenericRDFGenerator.InputType;
import edu.isi.karma.rdf.RDFGeneratorRequest;
import edu.isi.karma.webserver.KarmaException;

/**
 * @author omar
 * @todo Purpose of the class
 */

public class RDFConvertor {

	/**
	 * This method converts the AML files into RDF. The default conversion is in N triples. We have to manually add the URI for the triples. 
	 * We then  apply the Turtle format for better Readability.
	 * @throws URISyntaxException
	 * @throws KarmaException
	 * @throws IOException
	 */
	public void convertor() throws URISyntaxException, KarmaException, IOException {

		// Setting properties
		GenericRDFGenerator rdfGenerator = new GenericRDFGenerator();

		// identifier requires model name, and model file for conversion.
		R2RMLMappingIdentifier modelIdentifier = new R2RMLMappingIdentifier("model",
				getTestResource("model.ttl").toURI().toURL());

		// Adds Model
		rdfGenerator.addModel(modelIdentifier);

		int i = 0;

		// Loop through selected files and generate RDF in Ntriples without URI
		while (i < RdfGUI.files_.length) {
			// setting up configuration

			FileWriter output = new FileWriter(getpath() + RdfGUI.files_[i].getName() + ".ttl");
			String filename = RdfGUI.files_[i].getAbsolutePath();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			N3KR2RMLRDFWriter writer = new N3KR2RMLRDFWriter(new URIFormatter(), pw);
			RDFGeneratorRequest request = new RDFGeneratorRequest("model", filename);
			request.setInputFile(new File(filename));
			request.setAddProvenance(false);
			request.setDataType(InputType.XML);
			request.addWriter(writer);
			rdfGenerator.generateRDF(request);

			// writing the RDF
			output.write(sw.toString());
			output.close();

			// RDF generated does not include URI adding manually
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(getpath() + RdfGUI.files_[i].getName() + ".ttl"));

			try {
				String line = br.readLine();

				while (line != null) {
					// adding the missing URI
					String line2 = line.replaceAll("<(?![<http]+)", "<http://iais.fraunhofer.de/aml#");
					sb.append(line2);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
			} finally {
				br.close();
			}
			output = new FileWriter(getpath() + RdfGUI.files_[i].getName() + ".ttl");
			output.write(sb.toString());
			output.close();

			// conversion to Turtle format
			Model modelX = FileManager.get().loadModel(getpath() + RdfGUI.files_[i].getName() + ".ttl");
			sw = new StringWriter();
			RDFDataMgr.write(sw, modelX, RDFFormat.TURTLE_BLOCKS);
			output = new FileWriter(getpath() + RdfGUI.files_[i].getName() + ".ttl");
			output.write(sw.toString());
			output.close();
			i++;

		}
	}

	/**
	 * Gets the path from where the aml files were selected and saves in output
	 * @return
	 */
	public String getpath() {
		String path = RdfGUI.files_[0].getParent() + "\\" + "output\\";
		return path;
	}

	/**
	 * Gets the resource folder
	 * @param name
	 * @return
	 */
	protected URL getTestResource(String name) {
		return getClass().getClassLoader().getResource(name);
	}
}
