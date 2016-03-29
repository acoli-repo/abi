package in2rdf;

/** 
reads a file in CONLL-like annotations produced by Splitta (tokens, sentences), TreeTagger (lemma), Senna (POS, PSD, NER, CHK, SRL) and LTH (Penn converter, for dependencies)
<br/>
only annotation-/language-specific components are implemented, for general methods, see AbstractSentenceImporter<br><br>

input format tab-separated text (CSV), <br>
- rows are words + annotations, empty for sentence boundary<br>
- cols are annotations (see public final int variables in constructor)<br><br>

output triples that capture general (flat) semantics<br>
- structures are deliberately close to surface, use verbal predicates and prepositions as properties instead of formal definitions<br><br>

<h1>TODOs:</h1>
<ul>
<li> cover genitive constructions (using the predicate "of")
<li> cover copular sentences for different mood and aspect (example for copular relative clauses in indicative below)
<li> cover all prepositions (e.g., TO where used as preposition)
<li> NP coreference (same label)
<li> pronominal coreference (using an external tool, say, from StanfordCore)
<li> link with archeology vocabulary
</ul>
*/

import java.io.*;
import java.util.*;

import Helper.listFiles;
import Helper.readwriteFiles;

/**import acoli.nlp.Lemmatizer;*/

public class Sentence2Triple extends AbstractSentenceImporter {
	
	/** number of blank nodes generated so far */
	private int blanks = 0;

    public static void main(String[] argv, String filelocation) throws Exception {
		Sentence2Triple me = new Sentence2Triple();
    	//if(argv.length>0 && argv[0].toLowerCase().contains("-debug"))
			//me.setDebug(true);
       //test  
		//Helper.readwriteFiles hr = new Helper.readwriteFiles();
		//String s = hr.read1("/home/kathrin/workspace/PraktikumWS1516/localFiles/outputPipeline/en/mate/" +
		//"test.txt");
//		me.sentence2turtle(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
		//liest von eingabe --> Umschreiben, sodass von file liest 
        //System.out.print(s);
		readwriteFiles rwf = new readwriteFiles();
		listFiles lf = new listFiles();
		List<File> files = lf.listf(filelocation);
		for(File f : files){
			String filename = f.getAbsolutePath();
			Reader in = new FileReader(filename);
		
			Writer fw = null;
			String outputfile = filename.split("/mate/")[0]+"/rdf/"+filename.split("/mate/")[1].split(".txt")[0]+".ttl";
			System.out.print(outputfile);
			fw = new FileWriter(outputfile);
			
			me.sentence2turtle(in, fw);}
        //SortedSet<String> f = me.sentence2turtle(s, 10);
   
    }

	protected String getPredicate(String[][] fields, Set<String> roles, int i) {
		String result=getTerm(fields, i);
		if(roles.contains("AM-NEG")) result=result.replaceFirst(":",":not-");
		return result;
	}
	
    @SuppressWarnings("static-access")
	public SortedSet<String> sentence2turtle(String s, int sentenceNr)  {
    	//System.out.print(s);
        String fields[][] = split(s);
        //System.out.print(fields.length);
        //System.out.print(fields[1][0]+" "+fields[2][1]);
		SortedSet<String> result = new TreeSet<String>();
		HashSet<String> processedNPs = new HashSet<String>(); // log processed NPs to avoid redundancy between NP processing and SRL ARG processing

        // add lemmatization (if missing)
        Boolean lemmatized = null;
        for(int i = 0; i<fields.length && lemmatized==null; i++)
        	if(fields[i].length>lem)
        		lemmatized=(!fields[i][lem].replaceAll("[_\\-]","").trim().equals(""));
                System.err.print(" Lemmatized: "+lemmatized+" ");
        /**if(lemmatized==null || !lemmatized) {
        	lemmatizer.lemmatize(fields, word, pos, lem);
        }*/
        
		// relation extraction within all NPs, using chunking and dependency annotation
         //System.out.print("hallo, hier ist die field length: "+fields.length);
//		for(int line = 0; line<fields.length; line++){
//			//System.out.print(fields[line][1]+"\n");
//		
//			if(fields[line][chk].trim().matches("^[ES]-[NP]P$") && fields[line][pos].trim().startsWith("N")) {
//				String id = getId(fields, line, sentenceNr);
//				if(!processedNPs.contains(id))
//					result.addAll(npTriples(fields, id, line, sentenceNr));
//			}
//		}
		// relation extraction with PropBank-style semantic role labeling (most verbs)
		// plus relation extraction for "be" if used as predicate (from PCFG parse plus unlabeled dependencies)
		int arg = pred+1; // first arg col is right after pred
        for(int i = 0; i < fields.length; i++){
//            if(fields[i][pred].matches(".*[a-zA-Z].*") ||  				// using SRL for most verbs
//            	(														// or "be" predicate
//            		fields[i][lem].trim().equals("be") &&					// predicates are innermost direct children of a VP node, i.e., either (VP*) or (VP*(X.. with X being not a VP 
//    				( fields[i][psd].trim().contains("(VP*)") ||			// nominal argument
//    				  fields[i][psd].trim().equals("*") ||					// no nominal argument
//    					( fields[i][psd].trim().contains("(VP*") && 
//    					  i+1<fields.length && 
//    					  !fields[i+1][psd].trim().startsWith("(VP") ) ) ) ) {

            	
            	// map every role to the associated lines
            	Hashtable<String,List<Integer>> role2line = new Hashtable<String,List<Integer>>();
            	if(fields[i][pred].matches(".*[a-zA-Z].*")) { 		// using SRL (most verbs)
	            	for(int j = 0; j < fields.length; j++) {
	                    String role = fields[j][arg].replaceFirst("^[^-]*-", "");
	                    if(!fields[j][arg].trim().equals("O") && !role.equals("V")) {
	                        if(role2line.get(role) == null)
	                            role2line.put(role, new ArrayList<Integer>());
	                        
	                        role2line.get(role).add(j);
	                    System.err.print(role+":"+role2line.get(role).size()+" ");
	                    }
	                }
	            	arg++;	// next predicate, next SRL argument col
            	} else {											// predicative "be"
            		if(fields[i][lem].trim().equals("be")) {
            			// A0 argument is nominal subject, i.e., first nominal or adjectival dependent 
            			for(int j = 0; j<i && role2line.get("A0")==null; j++)
            				if(fields[j][head].trim().equals(fields[i][nr].trim()) && fields[j][pos].trim().matches("^[JNP].*")) 
            					role2line.put("A0", Arrays.asList(new Integer[] { j }));
            			// A1 argument is the predicate, i.e., last nominal or adjectival dependent
            			for(int j = fields.length-1; j>i && role2line.get("A1")==null; j--)
            				if(fields[j][head].trim().equals(fields[i][nr].trim()) && fields[j][pos].trim().matches("^[JNP].*")) 
            					role2line.put("A1", Arrays.asList(new Integer[] { j }));
            			// AM-NEG argument is direct child with lemma "not" or "never" or A1 lemma with "no(body|where|...)" or A1 child with "no"
            			for(int j = 0;j<fields.length && role2line.get("AM-NEG")==null; j++)
            				if(fields[j][lem].trim().matches("^(not|never|n't)$") && fields[j][head].trim().equals(fields[i][nr].trim()))
            					role2line.put("AM-NEG", Arrays.asList(new Integer[] { j }));
            			if(role2line.get("AM-NEG")==null && role2line.get("A1")!=null) 
            				for(int k = 0; k<role2line.get("A1").size() && role2line.get("AM-NEG")==null; k++) {
            					if(fields[role2line.get("A1").get(k)][lem].trim().matches("^(no|none|noone|no-one)$"))		// check heads and spans
            						role2line.put("AM-NEG", Arrays.asList(new Integer[] { k }));
            					for(int l = 0; l<fields.length && role2line.get("AM-NEG")==null; l++) 						// check modifiers of heads
            						if(fields[l][head].trim().equals(fields[k][nr].trim()) && fields[l][lem].trim().equals("no"))
            							role2line.put("AM-NEG", Arrays.asList(new Integer[] { l }));
            				}
            			// add prepositional arguments (assume that prepositions are heads, as in pennconverter)
            			for(int j = 0; j<fields.length; j++)
            				if(fields[j][head].trim().equals(fields[i][nr].trim()) && fields[j][pos].trim().matches("^(IN|TO)$")) 
            					role2line.put(fields[j][lem], Arrays.asList(new Integer[] { j }));
            		}
            	}
            	
				// reduce argument role fillers to heads in the dependency representation
				Hashtable<String,List<Integer>> reducedrole2line = new Hashtable<String,List<Integer>>();
				for(String role : role2line.keySet()) {
					reducedrole2line.put(role,new ArrayList<Integer>());
					for(Integer a : role2line.get(role)) 			// remove semantically empty elements, reduce to heads
						if(!functionalNode(fields[a]) && !hasParentInField(fields, a, role2line.get(role), true))
							reducedrole2line.get(role).add(a);
				}
				role2line=reducedrole2line;

				// relation extraction from Semantic Role Labeling annotations
                String predicate = getPredicate(fields, role2line.keySet(), i);
				String agent = getId(fields, role2line.get("A0"), sentenceNr);
				System.out.print(agent);
				String patient = getId(fields, role2line.get("A1"), sentenceNr);
				Hashtable<String,String> role2id = new Hashtable<String,String>();
				for(String role : role2line.keySet())
					if(!role.matches("^(O|A[01])$") && role2line.get(role)!=null && role2line.get(role).size()>0)
						role2id.put(role, getId(fields, role2line.get(role), sentenceNr));

				if(agent!=null || patient!=null) {
					
					// relation extraction with semantic roles
					if(agent==null && patient!=null) agent="_:n"+(++blanks);
					if(agent!=null && patient==null) patient="_:n"+(++blanks);
					
					result.add(agent+"\t"+predicate+"\t"+patient+".");
					result.add(agent+"\t:do\t"+patient+".");
					if(!agent.startsWith("_:n"))
						result.addAll(agOthTriples(agent, role2line, fields, sentenceNr));
					if(!patient.startsWith("_:n"))
						result.addAll(patOthTriples(patient, role2line, fields, sentenceNr));
				
					// relation extraction within ARGs (if not yet covered by the NP processing)
					role2id.put("A0",agent);
					role2id.put("A1",patient);
					for(String role : role2line.keySet()) {
						String id = role2id.get(role);
						if(role2line.get(role).size()>0 && !processedNPs.contains(id)) {
							processedNPs.add(id);
							int line = role2line.get(role).get(0); // consider the first head only
							result.addAll(npTriples(fields, id, line, sentenceNr));
						}
					}
				}
            }
//        System.err.print(".");
        System.out.print(result);
		return result;
    }
	
	protected SortedSet<String> npTriples(String[][] fields, String id, int line, int sentenceNr) {
		TreeSet<String> result = new TreeSet<String>();
		
		if(functionalNode(fields[line])) {
			for(Integer c : getChildren(fields, line, true))
				result.addAll(npTriples(fields,id,c,sentenceNr));
		} else {
			result.add(id+"\trdfs:label\t\""+toString(fields, line).replaceAll("&","&amp;").replaceAll("\"","&quot;")+"\"^^xsd:string.");
			
			if(fields[line][pos].trim().matches("^NN[^P]*[S]*$"))
				result.add(id+"\ta\t"+getTerm(fields, line)+".");
//			if(fields[line][ner].trim().endsWith("PER"))
//				result.add(id+"\ta\t:person.");
//			else if(fields[line][ner].trim().endsWith("ORG"))
//				result.add(id+"\ta\t:organization.");
//			else if(fields[line][ner].trim().endsWith("LOC"))
//				result.add(id+"\ta\t:place.");
//			else if(fields[line][ner].trim().endsWith("MISC"))
//				result.add(id+"\ta\t:entity.");
			else if(fields[line][pos].trim().equals("CD"))
				result.add(id+"\ta\t:number.");
			else if(fields[line][pos].trim().startsWith("V"))
				result.add(id+"\ta\t"+getTerm(fields,line)+".");
				// result.add(id+"\ta\t:event.");
			
			if(fields[line][pos].trim().matches("^N[^S]*$")) 
					result.add(id+"\t:cardinality\t\"1\"^^xsd:int."); 
			
			for(Integer c : getChildren(fields, line, false)) { // these are direct children, e.g., IN node for PPs
				if(fields[c][pos].trim().startsWith("JJ")) 
					result.add(id+"\t:property\t"+getTerm(fields,c)+".");
				if(fields[line][lem].trim().equals("no")) 
					result.add(id+"\t:cardinality\t\"0\"^^xsd:int."); 
				if(fields[c][pos].trim().startsWith("CD")) {
					try {
						int i = Integer.parseInt(fields[c][word]);
						result.add(id+"\t:cardinality\t\""+i+"\"^^xsd:int.");
					} catch (NumberFormatException e) {
						result.add(id+"\t:cardinality\t\""+fields[c][word]+"\".");
					}
				}
				if(fields[c][lem].trim().equals("be")) {				// limited to copular relative clauses, e.g., "the Via Flamina, which was in this portion ..."
					for(Integer c2 : getChildren(fields, c, true)) {
						if(fields[c2][pos].trim().equals("IN")) {
							String relation = getTerm(fields, c2);
							for(Integer c3: getChildren(fields, c2, false))
								if(fields[c3][pos].trim().startsWith("N")) {
									String cId = getId(fields, c3, sentenceNr);
									result.add(id+"\t"+relation+"\t"+cId+".");
									result.addAll(npTriples(fields, cId, c2, sentenceNr));
								}
						}
						if(fields[c2][pos].trim().matches("^N[^P]*[S]*$"))
							result.addAll(npTriples(fields, id, c2, sentenceNr));		// NP information directly applied to the controller in the main clause (= id)
						if(fields[c2][pos].trim().matches("^(JJ.*)$"))
							result.add(id+"\t:property\t"+getTerm(fields,c2)+".");
						if(fields[c2][pos].trim().equals("CD")) {
							try {
								int i = Integer.parseInt(fields[c2][word]);
								result.add(id+"\t:cardinality\t\""+i+"\"^^xsd:int.");
							} catch (NumberFormatException e) {
								result.add(id+"\t:cardinality\t\""+fields[c2][word]+"\".");
							}
						}
					}
				}
	
/*
		the example covers the following case, it will fail, however, for perfect sentences
		
							/ the
						Via
							\ Flaminia
							\ ,
								/ which
							\ was
								\ in
										/ this
									\ portion
								\ at
									\ least
									\ ,
										/ a
										/ typical
									\ Roman
										\ via
											\ glareata
				*/

				/* TODO: cover other copular sentences, e.g.
															
					/ they
				have
					\ been 
						\ in
								/ recent
							\ decades
				*/
				
				if(fields[c][pos].trim().equals("IN")) {
					String relation = getTerm(fields, c);
					for(Integer grandc : getChildren(fields, c, true)) {
						String cId = getId(fields, grandc, sentenceNr);
						result.add(id+"\t"+relation+"\t"+cId+".");
						result.addAll(npTriples(fields, cId, grandc, sentenceNr));
					}
				}
			}
			// todo: genitive
		}
		return result;
	}
	
	/** we expect pruned role2line, i.e., heads only <br/>
	    semantic roles are mapped to conventional expressions: TMP => during, MNR => (act_)like, ADV => like, LOC => at, PNC => to, CAU => as-result
	    in order to facilitate generalization, we encode semantic roles with the generic predicate ":do" instead of the actual predicate 
	 */
	protected SortedSet<String> agOthTriples(String agent, Hashtable<String, List<Integer>> role2line, String[][] fields, int sentenceNr) {
		TreeSet<String> result = new TreeSet<String>();
		String predicate = ":do";
		if(agent!=null)
		for(String role : role2line.keySet())
			if(!role.matches("^(A[01]|AM-NEG)$")) {
				String target = getId(fields, role2line.get(role), sentenceNr);
				if(target!=null) {
					if(role.contains("AM-LOC"))
						result.add(agent+"\t"+":at"+"\t"+target+".");
					else if(role.contains("AM-MNR")) {
						result.add(agent+"\t"+":act_like"+"\t"+target+".");
						result.add(agent+"\t"+predicate+"_like\t"+target+".");
					} else if(role.contains("AM-ADV"))
						result.add(agent+"\t"+predicate+"_like\t"+target+".");
					else if(role.contains("AM-PNC"))
						result.add(agent+"\t"+predicate+"_to\t"+target+".");
					else if(role.contains("AM-TMP"))
						result.add(agent+"\t"+":during"+"\t"+target+".");
					else if(role.contains("AM-"))
						result.add(agent+"\t"+predicate+"_"+role.replaceFirst("^AM-","")+"\t"+target+".");
					for(Integer a : role2line.get(role)) {
						int parent = Integer.parseInt(fields[a][head])-1;
						if(parent>-1 && fields[parent][pos].trim().equals("IN")) {
							result.add(agent+"\t"+predicate+"_"+fields[parent][lem]+"\t"+target+".");
							if(role.contains("AM-")) 
								result.add(agent+"\t"+getTerm(fields, parent)+"\t"+target+".");
						}					
					}
				}
			}
		return result;
	}

	/** we expect pruned role2line, i.e., heads only <br/>
	    semantic roles are mapped to conventional expressions: TMP => during, MNR => (act_)like, ADV => like, LOC => at, PNC => to, CAU => as-result
	    in order to facilitate generalization, we encode semantic roles with the generic predicate ":do" instead of the actual predicate 
	 */
	protected SortedSet<String> patOthTriples(String patient, Hashtable<String, List<Integer>> role2line, String[][] fields, int sentenceNr) {
		SortedSet<String> result = new TreeSet<String>();
		String predicate = ":do";
		if(patient!=null)
		for(String role : role2line.keySet()) 
			if(!role.matches("^(A[01]|AM-NEG)$")) {
				String target = getId(fields, role2line.get(role), sentenceNr);
				if(target!=null) {
					if(role.contains("AM-LOC"))
						result.add(patient+"\t"+":at"+"\t"+target+".");
					else if(role.contains("AM-MNR"))
						result.add(target+"\t"+predicate.replaceFirst(":",":like_")+"\t"+patient+".");
					else if(role.contains("AM-ADV"))
						result.add(target+"\t"+predicate.replaceFirst(":",":like_")+"\t"+patient+".");
					else if(role.contains("AM-PNC"))
						result.add(target+"\t"+predicate.replaceFirst(":",":to_")+"\t"+patient+".");
					else if(role.contains ("AM-TMP"))
						result.add(patient+"\t"+":during"+"\t"+target+".");
					else if(role.startsWith("AM-"))
						result.add(target+"\t"+predicate.replaceFirst(":",":"+role.replaceFirst("^AM-","")+"_")+"\t"+patient+".");
					for(Integer a : role2line.get(role)) {
						int parent = Integer.parseInt(fields[a][head])-1;
						if(parent>-1 && fields[parent][pos].trim().equals("IN")) {
							result.add(target+"\t"+getTerm(fields,parent)+"_"+predicate.replaceFirst(".*:","")+"\t"+patient+".");
							if(role.contains("AM-")) 
								result.add(patient+"\t"+getTerm(fields,parent)+"\t"+target+".");
						}
					}
				}
			}
		return result;
	}

	/** functional nodes are words that may represent grammatical heads, but not semantic heads */
	protected boolean functionalNode(String[] line) {
		return  line[pos].trim().matches("^(IN|RB|DT|CC|,|TO|MD.*)$") ||  // grammatical categories // we need WH pronouns for SPARQL ! 
				line[lem].trim().matches("^(be|have|do)$") || 				// aux. verbs
				line[lem].trim().matches("^presence|location$");			// presence of X => X, location of X => X
	}
	
}