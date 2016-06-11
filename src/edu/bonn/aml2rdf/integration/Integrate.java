/*
 * @class Integration.java
 * 
 * @Version =1.0
 *
 * @Date 4/11/2016
 * 
 * @Copyright EIS University of Bonn */

package edu.bonn.aml2rdf.integration;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.bonn.aml2rdf.gui.RDFGUI;
import edu.bonn.aml2rdf.rdfconvertor.RDFConvertor;
import nu.xom.Builder;
import nu.xom.Serializer;

/**
 * @author Omar This Class Automatically integrates RDF and convert it to
 *         AutomationML(ML) file format.
 */
public class Integrate {

	private FileWriter output;
	public final static String AML_NAMESPACE = "http://iais.fraunhofer.de/aml#";

	/**
	 * This method integrates the RDF files into single RDF, The default
	 * conversion is in in Turtle format. Integration is performed through an
	 * SPARQL Query.
	 * 
	 * @throws IOException
	 */
	public void integrateRDF() throws IOException {

		// Sets output file of RDF integration
		output = new FileWriter(new RDFConvertor().getpath() + "integration.aml.ttl");
		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, getIntegratedModel(), RDFFormat.TURTLE_BLOCKS);
		output.write(sw.toString());
		output.close();

		// Sets output format RDF/XML for XML conversion
		output = new FileWriter(new RDFConvertor().getpath() + "integration.rdf");
		sw = new StringWriter();
		RDFDataMgr.write(sw, getIntegratedModel(), RDFFormat.RDFXML);
		output.write(sw.toString());
		output.close();

		try {

			// calls for XML conversion for AML files.
			convertXML(getIntegratedModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new RDF graph using Construct Query.
	 * 
	 * @return model
	 */
	protected Model getIntegratedModel() {

		// loads two RDF files in turtle format from output folder.
		Model firstModel = FileManager.get()
				.loadModel(new RDFConvertor().getpath() + RDFGUI.files[0].getName() + ".ttl");
		Model secondModel = FileManager.get()
				.loadModel(new RDFConvertor().getpath() + RDFGUI.files[1].getName() + ".ttl");

		// gives those two files a URI for querying data
		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(firstModel);
		dataset.addNamedModel(AML_NAMESPACE + "Hetrogenity-1", firstModel);
		dataset.addNamedModel(AML_NAMESPACE + "Hetrogenity-2", secondModel);

		String queryString = null;

		// Reads the Query from file
		try (InputStream res = Integrate.class.getResourceAsStream("/M1.2Query.rq")) {
			queryString = IOUtils.toString(res);
		} catch (IOException e) {
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

	/**
	 * This method converts the RDF file into XML. First of all generalized
	 * conversion is done for RDF. After that XML is processed in domain of AML
	 * files only. The output XML is only compatible for AML files and not
	 * generalized RDF. The output is processed according to AML elements and
	 * attributes.
	 * 
	 * @param model
	 * @throws Exception
	 */
	void convertXML(Model model) throws Exception {

		ArrayList<String> cNodes = rdfClasses();

		// Process XML file to remove RDF serialization.
		processXML(cNodes);

		// DOM parser to Read XML file to add Attribute and Elements
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		// Path to generated XML without RDF
		Document doc = dBuilder
				.parse(new FileInputStream(new File(new RDFConvertor().getpath() + "integration.aml.rdf")));
		doc.getDocumentElement().normalize();

		// get attributes from original file
		ArrayList<String> tempNodes = getAttributes();

		// for every attribute get its node name
		for (int i = 0; i < tempNodes.size(); i++) {
			NodeList nodeList = doc.getElementsByTagName(tempNodes.get(i));

			// for every node gets its parent
			for (int j = 0; j < nodeList.getLength(); j++) {
				Node node = nodeList.item(j);
				Element eElement = (Element) node.getChildNodes().item(0).getParentNode().getParentNode();

				// sets the attribute with parent and node value
				eElement.setAttribute(node.getNodeName(), node.getChildNodes().item(0).getNodeValue());

				// removes the nodes which were element attributes
				eElement.removeChild(node.getChildNodes().item(0).getParentNode());
			}
		}

		formatXML(doc);

	}

	/**
	 * This method gets the Classes from RDF graph using rdf:type property.
	 * 
	 * @return ArrayList.
	 */
	private ArrayList<String> rdfClasses() {
		// Array of nodes which will store all the classes of RDF graph.
		ArrayList<String> cNodes = new ArrayList<String>();

		// Getting the final RDF and reading all its graph to extract classes.
		Model modelY = FileManager.get().loadModel(new RDFConvertor().getpath() + "integration.aml.ttl");

		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(modelY);
		dataset.addNamedModel(AML_NAMESPACE + "M1.2", modelY);
		String queryString = null;

		// Query to read all the graph values
		try (InputStream res = (Integrate.class.getResourceAsStream("/M12query.rq"))) {
			try {
				queryString = IOUtils.toString(res);
			} catch (IOException e) {
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Query query = QueryFactory.create(queryString);

		try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				// Gets all the values of graph in variable
				RDFNode nPredicate = soln.get("predicate"); // Gets predicate
				RDFNode nObject = soln.get("object"); // Gets Object

				// Gets all rdf classes in array through rdf:type
				if (nPredicate.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
					String line = nObject.toString();

					// Removes the prefix and stores only name
					line = line.replaceAll("http://iais.fraunhofer.de/aml#", "");
					cNodes.add(line);
				}

			}
		}
		return cNodes;
	}

	/*
	 * Process the created RDF/XML format through JENA for AML. Reading the
	 * RDF/XML serialization
	 */
	private void processXML(ArrayList<String> cNodes) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new RDFConvertor().getpath() + "integration.rdf"));
		try {
			String line = br.readLine(); // reads line by line.

			// Removes white spaces between XML tags.
			while (line != null) {
				while (!line.contains(">")) {
					line += br.readLine();
				}

				// Removes Unwanted URI's xmlns and RDF.
				if (!line.contains("xmlns:") && !line.contains("rdf:RDF")) {
					if (line.contains("aml:")) {
						line = line.replaceAll("aml:", "");
					}
					// removes Schema URI
					if (line.contains("schema:name")) {
						line = line.replaceAll("schema:name", "Name");
					}

					if (line.contains("j.0")) {
						line = line.replaceAll("j.0", "xmlns");
					}

					// Removes rdf:about column as its not needed for AML
					String temp = StringUtils.substringBetween(line, "rdf", ">");
					line = line.replaceAll("rdf" + temp, "");

					// Sets a flag values to false if Classes are matched.
					boolean flag = true;
					for (int i = 0; i < cNodes.size(); i++) {
						// matches has+Class+Name.
						if (!line.contains("has" + cNodes.get(i) + "DataType"))
							if (line.contains("has" + cNodes.get(i))) {
								flag = false; // Class found.
							}
					}
					// Removes all properties starting with has
					if (line.contains("has")) {
						line = line.replaceAll("has", "");
					}

					line = line.replaceAll("\\s", "");

					// If Class found ignore that line.
					if (flag) {
						sb.append(line);
						sb.append(System.lineSeparator());
					}

				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}

		// After processing, output the generalized XML file without RDF.
		output = new FileWriter((new RDFConvertor().getpath() + "integration.aml.rdf"));
		output.write(sb.toString());
		output.close();
	}

	/**
	 * @param doc
	 * @throws Exception
	 */
	private void formatXML(Document doc) throws Exception {

		// Writes the new added Elements Attributes with sylesheet sorting.
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream("sort.xsl")));
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(new RDFConvertor().getpath() + "integration.aml"));
		transformer.transform(source, result);

		FileInputStream res = new FileInputStream(new RDFConvertor().getpath() + "integration.aml");
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		String xml = IOUtils.toString(res);

		// formatting the XML
		Serializer serializer = new Serializer(out1);
		serializer.setIndent(4); // or whatever you like
		serializer.write(new Builder().build(xml.toString(), ""));

		output = new FileWriter((new RDFConvertor().getpath() + "integration.aml"));
		output.write(out1.toString());
		output.close();

		File file = new File(new RDFConvertor().getpath() + "integration.aml.rdf");
		if (file.exists()) {
			file.delete();
		}
		file = new File(new RDFConvertor().getpath() + "integration.rdf");
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * This method Attributes values from original AML Files. These Values are
	 * then used to match the RDF values to identify as an AML Attribute
	 * Property. Integrated Values needed to be added manually.
	 * 
	 * @return
	 * @throws Exception
	 */
	ArrayList<String> getAttributes() throws Exception {
		ArrayList<String> cNodes = rdfClasses();
		ArrayList<String> attrNodes = new ArrayList<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		int j = 0;
		while (j < RDFGUI.files.length) {
			Document doc = dBuilder.parse(new FileInputStream(RDFGUI.files[j].getAbsolutePath()));
			doc.getDocumentElement().normalize();
			NodeList baseElmntLst = doc.getElementsByTagName("*");

			for (int k = 0; k < baseElmntLst.getLength(); k++) {
				// baseElmntLst =
				// doc.getElementsByTagName(cNodes.get(k).toString());
				Element baseElmnt = (Element) baseElmntLst.item(k);

				NamedNodeMap baseElmntAttr = baseElmnt.getAttributes();
				for (int i = 0; i < baseElmntAttr.getLength(); ++i) {
					Node attr = baseElmntAttr.item(i);
					if (!cNodes.contains(attr.getNodeName())) {
						if (j >= 1 && attrNodes.contains(attr.getNodeName())) {

						} else
							attrNodes.add(attr.getNodeName());
					}
				}
			}
			j++;
		}

		return attrNodes;
	}

}