/*  **************************************************************
 *   Autor(en)       : Christian Chiarcos,  Kathrin Donandt, Zhanhong Huang
 *  --------------------------------------------------------------
 *   copyright (c) 2016  Uni Frankfurt Informatik
 *   Alle Rechte vorbehalten.
 *  **************************************************************
 */
package in2rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**import acoli.nlp.Lemmatizer;*/

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

// TODO: add coreference, using owl:sameAs
// TODO: apply coreference/owl:sameAs 
// TODO: infer axioms from text, i.e., ?a :be|owl:sameAs ?b. ?a a ?ac. ?b a ?bc => ?ac rdfs:subClassOf ?bc 
/** provides <ul>
 * <li> field definitions for selected columns in a csv annotation format, e.g., CoNLL
 * <li> annotation-independent parts of extraction routines  
 * <li> aux. methods useful for import, e.g., term identification
 * </ul> 
 * subclasses have to implement annotation-/language-specific extraction routines
 * */ 

public abstract class AbstractSentenceImporter extends AbstractImporter{

	/** for lemmatization */
	/**protected final static Lemmatizer lemmatizer = new Lemmatizer(); */
		
	// column numbers, these should actually be static, but are dynamically initialized
    public final Integer nr;
    public final Integer word;
    public final Integer lem;
    public final Integer pos;
//    public final Integer ner ;
    //public final Integer chk ;
//    public final Integer psd ;
    public final Integer head ;
    public final Integer edge ;
    public final Integer pred ;
    // assume args after pred

    protected AbstractSentenceImporter (Integer nr, Integer word, Integer lem, Integer pos, Integer head, Integer edge, Integer pred){
   // (Integer nr, Integer word, Integer lem, Integer pos, Integer ner, 
    //		Integer chk, Integer psd, Integer head, Integer edge, Integer pred) {
	  this.nr = nr;
	  this.word = word;
	  this.lem = lem;
	  this.pos = pos;
//	  this.ner = ner;
	  //this.chk = chk;
	  //this.psd = psd;
	  this.head = head;
	  this.edge = edge;
	  this.pred = pred;
		// then args
    }
    
    protected AbstractSentenceImporter() {
    	//this(0,1,2,3,4,5,6,7,8,9);
    	this(0,1,2,4, 8,10, 13); //A0=14
    	//ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs 
    	// HEAD  (syntactic dependency),
    	//DEPREL (type of dependency), PRED (frame, roleset, sense, 
    	//or whatever it is called for a particular language), and 
    	//APREDs (PREDs' argument dependencies and labels).
    	// https://ufal.mff.cuni.cz/conll2009-st/task-description.html
    	
    }

    protected String getId(String fields[][], Integer p, int sentenceNr, String language)
    {
    	
    	
		String label = fields[p.intValue()][lem].trim();
		if(label.contains("@")) label=fields[p][word].trim();
		if(language.equals("en")){
		label = deAccent(label).replaceAll("[^a-zA-Z0-9]+", " ").trim().replaceAll(" ", "_");
		}
		else{
			label = deAccent(label).replaceAll("[^a-zA-Z0-9öäüßÄÖÜ]+", " ").trim().replaceAll(" ", "_");			
		}
		
		
		label=p+"_"+label;
        if(p<10) label="00"+label;
		else if(p<100) label = "0"+label;
		return "data:n"+sentenceNr+"_"+label;
    }

	/** use the first element in positions to determine the id. This means we loose some information, however ! */
    protected String getId(String fields[][], List<Integer> positions, int sentenceNr, String language) {
		if(positions==null) 		return null;
        if(positions.size() == 0)	return null;
        if(positions.size() == 1)	return getId(fields, positions.get(0), sentenceNr, language);
		
		// reduce to (first) head 
		Integer headCandidate = null;
        for(Integer p : positions) {
            try
            {
                int h = Integer.parseInt(fields[p][head]) - 1;
                if(!positions.contains(h))
                    if(headCandidate == null) {
                        headCandidate = p;
						return getId(fields, headCandidate, sentenceNr, language);
                    } else
                        headCandidate = -1;
            }
            catch(NumberFormatException numberformatexception) { }
        }
		
		return null; // doesn't happen
    }

	protected String getTerm(String language ,String[][] fields, int i) {

		if (language.equals("en")){
			return ":"+deAccent(fields[i][lem]).toLowerCase().replaceAll("[^a-zA-Z0-9]+"," ").trim().replaceAll(" ","_").replaceFirst("^([0-9])","_$1");

		}else{
			return ":"+deAccent(fields[i][lem]).replaceAll("[^a-zA-Z0-9äöüßÄÜÖ]+"," ").trim().replaceAll(" ","_").replaceFirst("^([0-9])","_$1");
				}
	}

	/** this is specific to a given annotation scheme, hence to be specified by the particular extractor */
    abstract boolean functionalNode(String[] line);
    
    /** this is specific to a given annotation scheme, hence to be specified by the particular extractor */
    abstract SortedSet<String> sentence2turtle(String s, int sentenceNr);     

    protected void sentence2turtle(String s, Writer out, int sentenceNr) throws IOException {
		for(String t : sentence2turtle(s,sentenceNr))
			out.write(t+"\r\n");
		out.flush();
    }

    /** default implementation for extracting SPARQL queries, calls and converts its output to query mode<br/>
     * SPARQL queries should relatively short, so we aggregate */
    public void sentence2sparql(Reader in, Writer out) throws IOException {
    	StringWriter ttl = new StringWriter();
    	sentence2turtle(in,ttl);
    	ttl2sparql(ttl.toString(), out);
    }
    
    /** converts canonically formatted ttl to SPARQL */
    public static void ttl2sparql(String ttl, Writer out) throws IOException {
    	String header = "";
    	String prolog = "";
    	String epilog = "";
    	String triples ="";
    	SortedSet<String> variables = new TreeSet<String>();
    	boolean inHeader = true;
    	for(String line : ttl.toString().split("\n")) {
    		if(inHeader)
    			header=header+(line.replaceAll("@prefix","PREFIX").replaceAll("@base", "BASE").trim().replaceAll("\\.$",""))+"\n";
    		if(inHeader && !line.contains("@")) inHeader=false;
    		if(!inHeader && !line.contains("rdfs:label")) {
    			line=line.replaceAll("#.*","")
    				.trim()
    				.replaceAll("^data:(n[0-9]+_[0-9]+_)", "?$1")
	    			.replaceAll("[ \t]data:(n[0-9]+_[0-9]+_)", " ?$1")
	    			.replaceAll("([.,;])$"," $1")
	    			.replaceAll("^[^\n]*[ \t]+a[ \t]:(any|some)(body|thing|where|how|one)[ \t]+\\.[ \t]*$","");	// remove indefinite quantifiers
    			String vars = line.replaceAll(" a "," ").replaceAll("[^ \t]*:[^ \t]*", "").trim();
    			// String firstVar = line.replaceAll("[ \t].*","").trim();			// creates huge result sets
    			if(!line.equals("")) {
    				//if(!firstVar.equals("") && variables.contains(firstVar)) {
    				//	triples=triples+"OPTIONAL { "+line+"}\n";
    				//} else
    					triples=triples+line+"\n";
    			}
	    		variables.addAll(Arrays.asList(vars.split("[ \t\n]"))); // the first statement about a new variable is obligatory
    		}
    	}
    	
    	prolog="SELECT DISTINCT";
    	for(String var : variables)
    		if(var.startsWith("?"))
    			prolog=prolog+" "+var+"_label";
    	prolog=prolog+"\nWHERE {";
    	
    	epilog = "";
    	for(String var : variables)
    		if(var.startsWith("?"))
    			epilog=epilog+var+" rdfs:label "+var+"_label .\n";
    	epilog=epilog+"}";
    	
    	if(variables.size()>0) {
    		out.write(header+"\n"+prolog+"\n"+triples+"\n"+epilog+"\n");
        	out.flush();
    	} else 
    		System.err.println("empty query");
    }
    
    /** default implementation for triple extraction, reads \n\n-separated sentence blocks and calls 
     * 	the (abstract) sentence2turtle(String, Writer, int) method */
    public void sentence2turtle(Reader in, Writer out) throws IOException    {
        BufferedReader bin = new BufferedReader(in);
		
		// write header
		writeHeader(out);		
		
		// write body
        int sentence = 0;
        String buffer = "";
        for(String line = bin.readLine(); line != null; line = bin.readLine())
            if(line.equals("")) {
                if(!buffer.equals("")) {
					if(!DEBUG) System.err.println();
					out.write("\n");
					sentence2turtle(buffer, out, sentence++);
                    buffer = "";
					out.write("\n#");
                }
            } else {
				if(DEBUG) {
					System.err.println(line);
				}
				out.write(line.replaceAll("^[^\t]*\t([^\t]*)\t.*","$1")+" ");
                buffer = buffer+line+"\n";
            }
			
        if(!buffer.equals("")) {
            sentence2turtle(buffer, out, sentence++);
			// System.err.println();
		}
    }
    
	/** 
	 * true if any (dependency-)ancestor of a (= line number) is in field<br/>
	 * if removeSemanticallyEmptyElements, require that this ancestor is not a function word or a semantically empty expression (in our domain!)   <br/>
	 * note that we check *all* ancestors, including those not in the fields!
	 */
	boolean hasParentInField(String[][] fields, Integer a, Collection<Integer> field, boolean removeSemanticallyEmptyElements) {
		return hasParentInField(fields, a, field, removeSemanticallyEmptyElements, 0);
	} 
	
	/** 
	 * aux. method for hasParentInField, extended to break cycles in dependency annotation
	 */
	protected boolean hasParentInField(String[][] fields, Integer a, Collection<Integer> field, boolean removeSemanticallyEmptyElements, int depth) {
		if(depth>fields.length) return false; // break cycles (shouldn't happen)
		int parent = -1;
		try {
			parent=Integer.parseInt(fields[a][head])-1;
		} catch (Exception e) {};
		if(parent==-1) return false;
		if(field.contains(parent)) {
			if(!removeSemanticallyEmptyElements)  return true;
			if(!functionalNode(fields[parent])) return true;
		}
		
		return hasParentInField(fields, parent, field, removeSemanticallyEmptyElements, depth+1);
	}
	
	protected ArrayList<Integer> getChildren(String[][] fields, int line, boolean removeSemanticallyEmptyElements) {
		ArrayList<Integer> me = new ArrayList<Integer>();
		me.add(line);
		ArrayList<Integer> dirChildren = new ArrayList<Integer>();
		for(int i = 0; i<fields.length; i++)
			if(Integer.parseInt(fields[i][head])-1==line) dirChildren.add(i);
		if(!removeSemanticallyEmptyElements) return dirChildren;
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i<fields.length; i++) 
			if(!functionalNode(fields[i]) && hasParentInField(fields, i, me, false) && !hasParentInField(fields, i, dirChildren, false))
				result.add(i);
		return result;
	}
	
	protected String toString(String[][] fields, int head) {
		SortedSet<Integer> field = new TreeSet<Integer>();
		field.add(head);
		for(int i = 0; i<fields.length; i++)
			if(hasParentInField(fields, i, field, false))
				field.add(i);
		String result = "";
		for(Integer i : field)
			result=result+fields[i][word].trim()+" ";
		return result.trim();
	}
	
    protected Model sentence2model(String s, int sentenceNr) {
    	Model result = ModelFactory.createDefaultModel();
    	StringWriter sWriter = new StringWriter();
    	try {
	    	writeHeader(sWriter);
	    	sentence2turtle(s, sWriter, sentenceNr);
	    	result.read(new StringReader(sWriter.toString()), "", "TTL");
    	} catch (IOException e) {
    		e.printStackTrace(); // shouldn't occur
    	}	
    	return result;
    }
    
    protected Model sentence2model(Reader in) {
    	Model result = ModelFactory.createDefaultModel();
    	try {
    		StringWriter sWriter = new StringWriter(); // how to circumvent this aggregation step ?
        	this.sentence2turtle(in, sWriter);
        	result.read(new StringReader(sWriter.toString()), "", "TTL");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return result;
    }

    /** split Conll-like input into a matrix */
	public static String[][] split(String s) {
        String lines[] = s.split("\n");
        String fields[][] = new String[lines.length][];
        for(int i = 0; i < lines.length; i++)
        		fields[i] = lines[i].split("\t");
                System.err.print(fields.length);
        return fields;
	}


}

