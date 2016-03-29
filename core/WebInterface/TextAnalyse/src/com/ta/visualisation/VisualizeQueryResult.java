package com.ta.visualisation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ta.bean.RWProperties;
import com.ta.resource.Globals;


public class VisualizeQueryResult {
	protected static  RWProperties rwpp = new RWProperties();
	public void visualizeTriple(String ttlfile_queryresult){

		String output = rwpp.getPropValues(Globals._PATH_TO_TRIPLES_JSON)+"triples.json";
		 String scriptlocation = rwpp.getPropValues(Globals._PATH_TO_CONVERTER) +"parse_triples.py";
			try {	
				ProcessBuilder pb = new ProcessBuilder("python", scriptlocation, ttlfile_queryresult,output);
				Process p;
				p = pb.start();
				String aLine = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((aLine = input.readLine()) != null) {
					 System.out.println(aLine);
				}
				input.close();
				
			} catch (FileNotFoundException e) {
				System.out.print(e.getMessage());
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}

		 }
	
public static void main(String[] args) {
	VisualizeQueryResult vq = new VisualizeQueryResult();
	vq.visualizeTriple("C:/Users/Annie/workspace/sample-sesame-remote/src/main/java/visualisation/converters/rdftriples.ttl");	//file in folder "triplesRDF"
	}
}
