/*  **************************************************************
 *   Projekt         : Text Analyse (java)
 *  --------------------------------------------------------------
 *   Autor(en)       : Kathrin Donandt, Zhanhong Huang
 *   Beginn-Datum    : 04.20.2016
 *  --------------------------------------------------------------
 *   copyright (c) 2016  Uni Frankfurt Informatik
 *   Alle Rechte vorbehalten.
 *  **************************************************************
 */
package xml2plaintxt;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import helper.listFiles;


/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class xml2txt {
		
		/**
		 * Uses XSLT file to extract the text out of an XML file
		 *
		 * @param input_path : absolute path of XML input file
		 * @param output_path : absolute path of txt output file
		 */
		
	    public void totxt(String input_path, String output_path, String locationProject) {  
	    	listFiles lf = new listFiles();
	  	   	List<File> files = lf.listf(input_path);
	  	   	String thisDir = locationProject+"src/xml2plaintxt/";
	  	   	String xslFile = thisDir+"xslFile.xsl";

	  	   	for (File f : files){
	  	   		try{
	  	   			String fp_input = f.getAbsolutePath();
	  	   			String fp_output = output_path+f.getAbsolutePath().split("/xml/")[1].split(".xml")[0]+".txt";
	    			TransformerFactory factory = TransformerFactory.newInstance();
	    	        Source xslt = new StreamSource(new File(xslFile));
	    	        Transformer transformer = factory.newTransformer(xslt);
	    	        Source text = new StreamSource(new File(fp_input));
	    	        transformer.transform(text, new StreamResult(new File(fp_output)));
	  	   		}catch(Exception e){
	  	   		e.printStackTrace();
	  	   		}
	  	   	}
	    }

		/**
		 * The main method.
		 *
		 * @param args the arguments
		 */
		public static void main(String[] args) {	
			
			}
}


	 

