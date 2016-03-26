/**

Copyright (C) SYSTAP, LLC 2006-2015.  All rights reserved.

Contact:
     SYSTAP, LLC
     2501 Calvert ST NW #106
     Washington, DC 20008
     licenses@systap.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * See <a href="http://wiki.blazegraph.com/wiki/index.php/Sesame_API_remote_mode">Sesame API remote mode</a>
 */

package SampleBlazegraphSesameRemote;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.bigdata.btree.ResultSet;
import com.bigdata.rdf.sail.webapp.SD;
import com.bigdata.rdf.sail.webapp.client.ConnectOptions;
import com.bigdata.rdf.sail.webapp.client.IPreparedGraphQuery;
import com.bigdata.rdf.sail.webapp.client.JettyResponseListener;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

import org.apache.log4j.Logger;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.turtle.TurtleWriter;

import properties.PropertiesSesame;
import visualisation.VisualizeQueryResult;
import visualisation.VisualizeQueryResultShell;

public class SampleBlazegraphSesameRemote {

	public static Logger log = Logger
			.getLogger(SampleBlazegraphSesameRemote.class);
	public static String serviceURL = "http://localhost:9999/bigdata";

	public static String queryExecutionGraph(String query) throws Exception {

		RemoteRepositoryManager repo = new RemoteRepositoryManager(serviceURL,
				false /* useLBS */);

		try {

			JettyResponseListener response = getStatus(repo);
			log.info(response.getResponseBody());

			// create a new namespace if not exists
			String namespace = "kb";
			Properties properties = new Properties();
			properties.setProperty("com.bigdata.rdf.sail.namespace", namespace);
			properties.setProperty("rdf", RDF.NAMESPACE);
			properties.setProperty("rdfs", RDFS.NAMESPACE);
			properties.setProperty("xsd", XMLSchema.NAMESPACE);
			properties.setProperty("foaf", FOAF.NAMESPACE);
			properties.setProperty("terms", "http://purl.org/acoli/open-ie/");
			properties.setProperty("owl", "http://www.w3.org/2002/07/owl#");
			properties.setProperty("skos",
					"http://www.w3.org/2004/02/skos/core#");
			properties.setProperty(":", "http://purl.org/acoli/open-ie/");
			properties
					.setProperty("data",
							"http://purl.org/acoli/open-ie/in2rdf.Sentence2Triple/1363910379#");
			if (!namespaceExists(repo, namespace)) {
				log.info(String.format("Create namespace %s...", namespace));
				repo.createRepository(namespace, properties);
				log.info(String.format("Create namespace %s done", namespace));
			} else {
				log.info(String
						.format("Namespace %s already exists", namespace));
			}

			// get properties for namespace
			log.info(String.format("Property list for namespace %s", namespace));
			response = getNamespaceProperties(repo, namespace);
			log.info(response.getResponseBody());
			/*
			 * Load data from file located in the resource folder
			 * src/main/resources/data.n3
			 */
			String resource = "data.n3";
			// loadDataFromResource(repo, namespace, resource);

			// execute query
			RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, System.out);

			IPreparedGraphQuery r = repo.getRepositoryForNamespace("kb")
					.prepareGraphQuery(query);
			GraphQueryResult result1 = r.evaluate();

			List<String> resultlist = new ArrayList();

			try {
				while (result1.hasNext()) {
					Statement st = result1.next();
					// System.out.print(st + "\n");
					String s = st.getSubject().toString();
					String p = st.getPredicate().toString();
					String o = st.getObject().toString();
					String spo = "<" + s + ">\t<" + p + ">\t<" + o + ">.\n";
					resultlist.add(spo);
					System.out
							.print("<" + s + ">\t<" + p + ">\t<" + o + ">.\n");

				}
			} finally {
				result1.close();
			}
			PropertiesSesame props = new PropertiesSesame();
			Properties propsPipeline = props.loadProps();
			String path_to_java = propsPipeline.getProperty("path_to_java");
			String outputfile = path_to_java
					+ "visualisation/converters/rdftriples.ttl";
			FileWriter fwriter = new FileWriter(outputfile);
			for (String str : resultlist) {
				fwriter.write(str);
			}
			fwriter.close();
			return outputfile;
		} finally {
			repo.close();
		}

	}

	public static String queryExecutionTuple(String query) throws Exception {

		RemoteRepositoryManager repo = new RemoteRepositoryManager(serviceURL,
				false /* useLBS */);

		try {

			JettyResponseListener response = getStatus(repo);
			log.info(response.getResponseBody());

			// create a new namespace if not exists
			String namespace = "kb";
			Properties properties = new Properties();
			properties.setProperty("com.bigdata.rdf.sail.namespace", namespace);
			properties.setProperty("rdf", RDF.NAMESPACE);
			properties.setProperty("rdfs", RDFS.NAMESPACE);
			properties.setProperty("xsd", XMLSchema.NAMESPACE);
			properties.setProperty("foaf", FOAF.NAMESPACE);
			properties.setProperty("terms", "http://purl.org/acoli/open-ie/");
			properties.setProperty("owl", "http://www.w3.org/2002/07/owl#");
			properties.setProperty("skos",
					"http://www.w3.org/2004/02/skos/core#");
			properties.setProperty(":", "http://purl.org/acoli/open-ie/");
			properties
					.setProperty("data",
							"http://purl.org/acoli/open-ie/in2rdf.Sentence2Triple/1363910379#");
			if (!namespaceExists(repo, namespace)) {
				log.info(String.format("Create namespace %s...", namespace));
				repo.createRepository(namespace, properties);
				log.info(String.format("Create namespace %s done", namespace));
			} else {
				log.info(String
						.format("Namespace %s already exists", namespace));
			}

			// get properties for namespace
			log.info(String.format("Property list for namespace %s", namespace));
			response = getNamespaceProperties(repo, namespace);
			log.info(response.getResponseBody());

			/*
			 * Load data from file located in the resource folder
			 * src/main/resources/data.n3
			 */
			// String resource = "data.n3";
			// loadDataFromResource(repo, namespace, resource);

			// execute query
			TupleQueryResult result = repo.getRepositoryForNamespace("kb")
					.prepareTupleQuery("SELECT * {?s ?p ?o} LIMIT 10")
					.evaluate();

			List<String> resultlist = new ArrayList();
			// result processing
			try {
				while (result.hasNext()) {
					BindingSet bs = result.next();
					log.info(bs);
					String n = bs.getBindingNames().toString();
					String o = bs.getValue("o").toString();
					String s = bs.getValue("s").toString();
					String p = bs.getValue("p").toString();
					String spo = "<" + s + ">\t<" + p + ">\t<" + o + ">.\n";
					resultlist.add(spo);
					System.out
							.print("<" + s + ">\t<" + p + ">\t<" + o + ">.\n");
				}
			} finally {
				result.close();
			}

			PropertiesSesame props = new PropertiesSesame();
			Properties propsPipeline = props.loadProps();
			String path_to_java = propsPipeline.getProperty("path_to_java");
			String outputfile = path_to_java
					+ "visualisation/converters/rdftriples.ttl";
			FileWriter fwriter = new FileWriter(outputfile);
			for (String str : resultlist) {
				fwriter.write(str);
			}
			fwriter.close();
			return outputfile;

		} finally {
			repo.close();
		}

	}

	public static String decideQueryType(String query) throws Exception{
		if(query.contains("SELECT")){
			return queryExecutionTuple(query);
		}
		else{
			return queryExecutionGraph(query);
		}
	}


	/*
	 * Status request.
	 */
	public static JettyResponseListener getStatus(
			final RemoteRepositoryManager repo) throws Exception {

		ConnectOptions opts = new ConnectOptions(serviceURL + "/status");
		opts.method = "GET";
		return repo.doConnect(opts);

	}

	/*
	 * Check namespace already exists.
	 */
	public static boolean namespaceExists(RemoteRepositoryManager repo,
			String namespace) throws Exception {

		GraphQueryResult res = repo.getRepositoryDescriptions();
		try {
			while (res.hasNext()) {
				Statement stmt = res.next();
				if (stmt.getPredicate().toString()
						.equals(SD.KB_NAMESPACE.stringValue())) {
					if (namespace.equals(stmt.getObject().stringValue())) {
						return true;
					}
				}
			}
		} finally {
			res.close();
		}
		return false;
	}

	/*
	 * Get namespace properties.
	 */
	public static JettyResponseListener getNamespaceProperties(
			RemoteRepositoryManager repo, String namespace) throws Exception {

		ConnectOptions opts = new ConnectOptions(serviceURL + "/namespace/"
				+ namespace + "/properties");
		opts.method = "GET";
		return repo.doConnect(opts);

	}

	/*
	 * Load data into a namespace.
	 */
	public static void loadDataFromResource(RemoteRepositoryManager repo,
			String namespace, String resource) throws Exception {

		// File f = new
		// File("/home/kathrin/workspace/sample-sesame-remote/src/main/resources/data.n3");
		// InputStream is = new
		// FileInputStream("/home/kathrin/workspace/sample-sesame-remote/src/main/resources/data.n3");
		InputStream is = SampleBlazegraphSesameRemote.class
				.getResourceAsStream(resource);

		if (is == null) {
			throw new IOException("Could not locate resource: " + resource);
		}
		try {
			;
			repo.getRepositoryForNamespace(namespace).add(
					new RemoteRepository.AddOp(is, RDFFormat.TURTLE));
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) throws Exception {
		String q = "PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>"
				+ "\n"
				+ "PREFIX tbox: <http://purl.org/acoli/open-ie/>"
				+ "\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "\n"
				+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "\n"
				+ " CONSTRUCT { ?aC ?r ?bC. ?bC ?s ?aC. ?cC ?t ?bC. ?bC ?u ?cC }"
				+ "\n"
				+ " WHERE { "
				+ " ?a ?ign ?c. ?c a ?cC. ?a a ?aC."
				+ " { ?a a tbox:king } UNION { ?c a tbox:king}."
				+ " {{ ?a ?r ?b } UNION { ?b ?s ?a}. ?b a ?bC.} UNION "
				+ " {{ ?c ?t ?b } UNION { ?b ?u ?c}. ?b a ?bC.}" + " }";

		String rdftriplefile = decideQueryType(q);
		//String rdftriplefile = queryExecutionGraph(q);
		VisualizeQueryResult vqr = new VisualizeQueryResult();
		vqr.visualizeTriple(rdftriplefile);

		String q1 = "SELECT * {?s ?p ?o} LIMIT 10";
		String rdftriplefile1 = decideQueryType(q1);
		//String rdftriplefile1 = queryExecutionTuple(q1);
		VisualizeQueryResult vqr1 = new VisualizeQueryResult();
		vqr1.visualizeTriple(rdftriplefile1);
		
	}
}
