package RDFTriple;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Helper.listFiles;
import Helper.readwriteFiles;

public class rdfExtraction {
	
	public List<String> sentenceSplit(String filepath){
		//Reads each line of the mate output file, sends it to the rdfPrepare function,
		//and saves the output to the list for the current sentence. 
		//When newline is encountered, we know that the sentence is finished, 
		//and send the sentence list to the rdfTriples function. 
		//The output is added to the list of sentences.
		File text = new File(filepath);
		List<String> sentences = new ArrayList<String>();
	    Scanner scnr;
		try {
			scnr = new Scanner(text); 
			List<String[]> sent = new ArrayList<String[]>();	
			while(scnr.hasNextLine()){
				String line = scnr.nextLine();
				if (line.isEmpty()){
					List<String[]> sent1 = sent;
					sentences.add(rdfTriples(sent1));
					sent.clear();
				//System.out.print(sentences+"\n");	
				}
				else{
					sent.add(rdfPrepare(line));
			}
	    }
		scnr.close();
	    return sentences;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return sentences;	
	}

	
	public String[] rdfPrepare(String line){
		//takes a mate line as input and only take the relevant information from it:
		//the lemma, the index, the index of the token it is related to, and the type of
		//its relationship with that token
		String[] line_split = line.split("\t");
		System.out.print(line_split[0]+line_split[1]+"\n");
		String[] triple = {line_split[0], line_split[2],line_split[4], line_split[8]};
		return triple;
	}
	
	public String rdfTriples(List<String[]> sent){
		//takes the sentence list, which contains each line of the mate file, processed by
		//the rdfPrepare function. Calculates the relationships (triples). 
		//The subject - predicate - object  of the triples are separated with a space. 
		//The triples themselves are separated by a newline.
		//Triple is returned as a String
		String triples = "";                     
		List<String[]> sent_rest = new ArrayList<String[]>();
		for(String[] line_org:sent){
			sent_rest = sent;
			Integer line_org_index = Integer.parseInt(line_org[0]);
			sent_rest.remove(line_org_index);
			Integer line_org_dest = Integer.parseInt(line_org[3]);
			for(String[] line_dest:sent_rest){
				if(Integer.parseInt(line_dest[0]) == line_org_dest){
					triples= triples+line_org[1]+" "+line_org[2]+" "+line_dest[1]+"\n";
				}
			}		
		}
		sent_rest = new ArrayList<String[]>(); 
		return triples;
	}
	
	public void writeRDF(String filepath, List<String> triples){
		//write RDF triples to file
		readwriteFiles rwf = new readwriteFiles();
		rwf.write(filepath, triples);
	}
	
	public void main(String path) {
		readwriteFiles rwf = new readwriteFiles();
		listFiles lf = new listFiles();
		List<File> files = lf.listf(path);
		for(File f : files){
			String f_path = f.getAbsolutePath();
			List<String> splitted = sentenceSplit(f_path);
			String f_path_rdfOutput = f_path.split("/mate/")[0]+"/rdf/"+f_path.split("/mate/")[1];
			rwf.write(f_path_rdfOutput, splitted);
		}
	 }
	
}
