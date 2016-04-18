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

import edu.bonn.aml2rdf.GUI.RdfGUI;
import edu.bonn.aml2rdf.rdfconvertor.RdfConvertor;
import nu.xom.Builder;
import nu.xom.Serializer;

public class Integrate {

	private FileWriter output;

	/**
	 * This method integrates the RDF files into single RDF, The default conversion is in in Turtle format. Integration is performed through SPARQL Query.
	 * @throws IOException
	 */
	public void integrateRDF() throws IOException {

		// Sets output file of RDF integration
		output = new FileWriter(new RdfConvertor().getpath() + "integration.aml.ttl");
		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, getNewModel(), RDFFormat.TURTLE_BLOCKS);
		output.write(sw.toString());
		output.close();

		// Sets output format RDF/XML for XML conversion
		output = new FileWriter(new RdfConvertor().getpath() + "integration.rdf");
		sw = new StringWriter();
		RDFDataMgr.write(sw, getNewModel(), RDFFormat.RDFXML);
		output.write(sw.toString());
		output.close();
		try {
			convertXML(getNewModel()); // calls for XML conversion for AML files.
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * creates a new RDF graph using Construct Query.
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

	/*
	 * This method converts the RDF file into XML , First of all generalized
	 * conversion is done for RDF. After that XML is processed in domain of AML
	 * files only. The output XML is only compatible for AML files and not
	 * generalized RDF. The output is processed according to AML elements and
	 * attributes.
	 * 
	 */

	void convertXML(Model model) throws Exception {

		ArrayList<String> cNodes = rdf_Classes();
		// Process XML file to remove RDF serialization.
		process_XML(cNodes);

		/* Array Holds Attribute, Values, and All Attributes */
		ArrayList<String> nAttribute = new ArrayList<String>();
		ArrayList<String> nValue = new ArrayList<String>();
		ArrayList<String> nAll_Attribute = new ArrayList<String>();
		// DOM parser to Read XML file to add Attribute and Elements
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// Path to generated XML without RDF
		Document doc = dBuilder
				.parse(new FileInputStream(new File(new RdfConvertor().getpath() + "integration.aml.rdf")));
		doc.getDocumentElement().normalize();

		Node temp_Node = null; // Temporary node to hold values.

		// Loop through all Classes and its attributes values.
		for (int j = 0; j < cNodes.size(); j++) {
			// Gets Elements based on the Class.
			NodeList nList = doc.getElementsByTagName(cNodes.get(j));

			// Loop through all Elements of Node and add attribute.
			for (int k = 0; k < nList.getLength(); k++) {
				Node nNode = nList.item(k);
				Element eElement = (Element) nNode;
				// add blank values.
				cNodes.add("#text");
				// Gets All the Child nodes of Element.
				NodeList child_Node = eElement.getChildNodes();

				// Loop through all the Child nodes.
				for (int n = 0; n < child_Node.getLength(); n++) {
					Node nNode1 = child_Node.item(n);

					// Skips if its a Class, else Add it as Attribute.
					if (!cNodes.contains(nNode1.getNodeName().toString())) {

						// Gets Attributes values from orignal AML files.
						ArrayList<String> tempNodes = getAttributes();
						// Manually Added integration Values
						if (tempNodes
								.contains(eElement.getElementsByTagName(nNode1.getNodeName()).item(0).getTextContent())
								|| nNode1.getNodeName().equals("DataType") || nNode1.getNodeName().equals("FileName")) {

							// Gets Attributes,Value and All nodes in Array.
							nAttribute.add(nNode1.getNodeName());
							nAll_Attribute.add(nNode1.getNodeName());

							// Loops Through Attribute and gets its Value.
							NodeList nListAtt = eElement.getElementsByTagName(nNode1.getNodeName());
							for (int m = 0; m < nListAtt.getLength(); m++) {
								Node nNode2 = nListAtt.item(m);

								// Checks if more than 2 Attributes Values.
								if (nListAtt.item(m + 1) == null) {
									// Adds the value of Attribute.
									nValue.add(eElement.getElementsByTagName(nNode2.getNodeName()).item(m)
											.getTextContent());
								}
							}
						}
					}
				}

				// Gets Element for adding Attribute.
				Element element = (Element) doc.getElementsByTagName(cNodes.get(j)).item(0);

				// Adds Attribute Name and Value.
				for (int i = 0; i < nAttribute.size(); i++) {
					element.setAttribute(nAttribute.get(i).toString(), nValue.get(i).toString());
				}

				// IF more than one child Elements exist, adds the previous.
				if (k >= 1) {
					temp_Node.getParentNode().insertBefore(nNode, temp_Node);
				}

				// Clears value for Next Class Node.
				nAttribute.clear();
				nValue.clear();
				temp_Node = nNode;

			}

		}

		// Removes All nodes which were Attributes in the Elements.
		for (int i = 0; i < nAll_Attribute.size(); i++) {
			NodeList list = doc.getElementsByTagName(nAll_Attribute.get(i).toString());
			for (int _i = list.getLength() - 1; _i >= 0; _i--) {
				Node nNode = list.item(_i);
				nNode.getParentNode().removeChild(nNode);
			}
		}

		formatXML(doc);

	}

	/**
	 * This method gets the Classes from RDF graph using rdf:type property.
	 * @return ArrayList.
	 */
	private ArrayList<String> rdf_Classes() {
		// Array of nodes which will store all the classes of RDF graph.
		ArrayList<String> cNodes = new ArrayList<String>();

		// Getting the final RDF and reading all its graph to extract classes.
		Model modelY = FileManager.get().loadModel(new RdfConvertor().getpath() + "integration.aml.ttl");

		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(modelY);
		dataset.addNamedModel("http://example/named-3", modelY);
		String queryString = null;

		// Query to read all the graph values
		try (InputStream res = (Integrate.class.getResourceAsStream("/test.txt"))) {
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
				RDFNode y = soln.get("y"); // Gets predicate by variable name.
				RDFNode z = soln.get("z"); // Gets Object by variable name.

				// Gets all rdf classes in array through rdf:type
				if (y.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
					String s = z.toString();

					// Removes the prefix and stores only name
					s = s.replaceAll("http://iais.fraunhofer.de/aml#", "");
					cNodes.add(s);
				}

			}
		}
		return cNodes;
	}

	/*
	 * Process the created RDF/XML format through JENA for AML. Reading the
	 * RDF/XML serialization
	 */
	private void process_XML(ArrayList<String> cNodes) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new RdfConvertor().getpath() + "integration.rdf"));
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
					if (line.contains("schema:")) {
						line = line.replaceAll("schema:", "");
					}
					// Capitals schema:name -> Name
					if (line.contains("name")) {
						line = line.replaceAll("name", "Name");
					}
					// Removes rdf:about column as its not needed for AML
					String temp = StringUtils.substringBetween(line, "rdf", ">");
					line = line.replaceAll("rdf" + temp, "");

					// Sets a flag values to false if Classes are matched.
					boolean flag = true;
					for (int i = 0; i < cNodes.size(); i++) {
						// matches has+Class+Name.
						if (!line.contains("has" + cNodes.get(i) + "Name"))
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
		output = new FileWriter((new RdfConvertor().getpath() + "integration.aml.rdf"));
		output.write(sb.toString());
		output.close();
	}

	private void formatXML(Document doc) throws Exception {

		// Writes the new added Elements Attributes.
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(new RdfConvertor().getpath() + "integration.aml"));
		transformer.transform(source, result);

		FileInputStream res = new FileInputStream(new RdfConvertor().getpath() + "integration.aml");
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		String xml = IOUtils.toString(res);

		// formatting the XML
		Serializer serializer = new Serializer(out1);
		serializer.setIndent(4); // or whatever you like
		serializer.write(new Builder().build(xml.toString(), ""));

		output = new FileWriter((new RdfConvertor().getpath() + "integration.aml"));
		output.write(out1.toString());
		output.close();

		File file = new File(new RdfConvertor().getpath() + "integration.aml.rdf");
		if (file.exists()) {
			file.delete();
		}
		file = new File(new RdfConvertor().getpath() + "integration.rdf");
		if (file.exists()) {
			file.delete();
		}
	}

	/*
	 * This method Attributes values from orignal AML Files. These Values are
	 * then used to match the RDF values to identify as an AML Attribute
	 * Property. Integrated Values needed to be added manually.
	 */

	ArrayList<String> getAttributes() throws Exception {
		ArrayList<String> cNodes = rdf_Classes();
		ArrayList<String> aNodes = new ArrayList<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		int j = 0;
		while (j < RdfGUI.files_.length) {
			Document doc = dBuilder.parse(new FileInputStream(RdfGUI.files_[j].getAbsolutePath()));
			doc.getDocumentElement().normalize();

			for (int k = 0; k < cNodes.size(); k++) {
				NodeList baseElmntLst_gold = doc.getElementsByTagName(cNodes.get(k).toString());
				Element baseElmnt_gold = (Element) baseElmntLst_gold.item(0);

				NamedNodeMap baseElmnt_gold_attr = baseElmnt_gold.getAttributes();
				for (int i = 0; i < baseElmnt_gold_attr.getLength(); ++i) {
					Node attr = baseElmnt_gold_attr.item(i);
					// System.out.println(attr.getNodeName() + " = \"" +
					// attr.getNodeValue() + "\"");
					if (!cNodes.contains(attr.getNodeName())) {
						aNodes.add(attr.getNodeValue());
					}
				}
			}
			j++;
		}

		return aNodes;
	}
}
