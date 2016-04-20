/*  **************************************************************
 *   Autor(en)       : Christian Chiarcos
 *  --------------------------------------------------------------
 *   copyright (c) 2016  Uni Frankfurt Informatik
 *   Alle Rechte vorbehalten.
 *  **************************************************************
 */
package in2rdf;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import com.hp.hpl.jena.rdf.model.*;

/** general aux. methods for Importers */
public abstract class AbstractImporter {

	private final static Pattern diacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	protected boolean DEBUG = false;

	/** true if belonging to a standard vocabulary 
	 * @return */
	public static boolean isSystemURI(String uri) {
		if(uri==null) return false; 			// blank nodes
		for(String suri : systemURIs)
			if(uri.startsWith(suri)) return true;
		return false;
	}
	
	/** system namespace prefixes, i.e., those that are written into the header along with data/abox and terms/tbox namespace */ 
	public static final String[] systemPfx = new String[] {
		"terms",
		"owl", 
		"skos",
		"rdf",
		"rdfs",
		"xsd"
	};
	
	/** system namespace URIs, i.e., those that are written into the header along with data/abox and terms/tbox namespace */
	public static final String[] systemURIs = new String[] {
		"http://purl.org/acoli/open-ie/",
		"http://www.w3.org/2002/07/owl#",
		"http://www.w3.org/2004/02/skos/core#",
		"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
		"http://www.w3.org/2000/01/rdf-schema#",
		"http://www.w3.org/2001/XMLSchema#"
	};
	
	
	protected void writeHeader(Writer out) throws IOException {
		String className = this.getClass().getName();
		try {
			String data = (new java.net.URI("http://purl.org/acoli/open-ie/"+className+"/"+System.identityHashCode(this)+"#")).toASCIIString();
			String tbox = //(new java.net.URI("http://purl.org/acoli/open-ie/"+className+"/")).toASCIIString();
				systemURIs[0];
			out.write(//"@base <"+tbox+"> .\n"+
					  "@prefix : <"+tbox+"> .\n"+
					  "@prefix data: <"+data+"> .\n");
			for(int i = 0; i<systemPfx.length; i++)
				out.write("@prefix "+systemPfx[i]+": <"+systemURIs[i]+">.\n");
			out.write("\n\n#");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.err.println("while initializing the URI \""+"http://purl.org/acoli/open-ie/"+className+"/"+System.identityHashCode(this)+"#"+"\"");
		}
	}
	
	/** produces a model preconfigured with the header from writeHeader */
	public Model newModel() {
		Model result = ModelFactory.createDefaultModel();
    	try {
    		StringWriter sWriter = new StringWriter(); 
        	this.writeHeader(sWriter);
        	result.read(new StringReader(sWriter.toString()), "", "TTL");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return result;
	}
	
	public static String deAccent(String str) {
		//String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
		//return diacritics.matcher(nfdNormalizedString).replaceAll("");
		return str;
	}

	/** if s is not yet a full URI or preceded by a registered namespace prefix, put it in the default namespace */
	protected String asURI(Model m, String s) {
		s=s.replaceAll(" ","_");
		if(s.matches("^[^\\/]*:[^\\/].*"))
			return asURI(m,s.replaceFirst("^[^:]*:", ""),s.replaceFirst(":.*",""));
		return asURI(m,s,"");
	}
	
	/** if s is not yet a URI, put it in the namespace with the specified prefix */
	protected String asURI(Model m, String s, String prefix) {
		s=s.replaceAll(" ","_");
		String uri = s;
		if(s.matches("^[^\\/]*:[^\\/].*"))
			return asURI(m,s.replaceFirst("^[^:]*:", ""),s.replaceFirst(":.*",""));
		if(!uri.contains(":/")) 
			uri=m.getNsPrefixMap().get(prefix)+deAccent(s);
		return uri;
	}

	
    protected void setDebug(boolean debug) {
    	DEBUG=debug;
    }
	
}
