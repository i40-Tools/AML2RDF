/**
 * @Copyright EIS University of Bonn
 */

package edu.bonn.aml2rdf.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 * The aim of this class is to load the RDF configuration file to this program containing all the input data
 * @class ConfigManager
 * @Version = 1.0
 * @Date 4/21/2016
 * @author Irlan
 **/

public class ConfigManager {

	private static ConfigManager manager;
	static Properties prop;
	private static RDFNode literal;
	private static RDFNode predicate;
	private static ArrayList<RDFNode> literals, predicates;
	private static Model model;
	
	public final static String HET_NAMESPACE = "http://vocab.cs.uni-bonn.de/het#";

	public static ConfigManager getInstance() {

		if (manager == null) {
			manager = new ConfigManager();

		}
		return manager;
	}

	/**
	 * This method load the Configuration file parameters
	 **/
	public static Properties loadConfig() {
		prop = new Properties();
		String dir = System.getProperty("user.dir");
		File configFile = new File(dir + "/configuration.ttl");

		if (configFile.isFile() == false) {
			System.out.println("Please especify the configuration file");
			System.exit(0);
		}

		model = ModelFactory.createDefaultModel();
		InputStream inputStream = FileManager.get().open(configFile.getPath());
		model.read(new InputStreamReader(inputStream), null, "TURTLE"); // parses an InputStream assuming RDF in Turtle format

		literals = new ArrayList<RDFNode>();
		predicates = new ArrayList<RDFNode>();

		StmtIterator iterator = model.listStatements();

		while (iterator.hasNext()) {

			com.hp.hpl.jena.rdf.model.Statement stmt = iterator.nextStatement();

			predicate = stmt.getPredicate();
			predicates.add(predicate);

			literal = stmt.getLiteral();
			literals.add(literal);

		}

		for (int i = 0; i < predicates.size(); ++i) {
			for (int j = 0; j < literals.size(); ++j) {
				String key = predicates.get(j).toString();
				String value = literals.get(j).toString();
				prop.setProperty(key, value);
			}
		}

		return prop;
	}
	
	public String getFilePath() {
		String filePath = loadConfig().getProperty(
				          HET_NAMESPACE + "path");
		return filePath;
	}

	/**
	 * @todo remove. Just for testing
	 * @param args
	 */
	public static void main(String[] args) {
 	    ConfigManager con = new ConfigManager();
		con.loadConfig();
		System.out.println(con.getFilePath() + "aaaa");
	}
   
}
