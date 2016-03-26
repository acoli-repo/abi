package Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import properties.PropertiesPipeline;
import SBD.*;
import RDFTriple.rdfExtraction;
import pdf2xml_own.pdf2xml;
import xml2plaintxt.*;
import html2txt.HtmlExtraction;


public class Pipeline {

	public static void main(String[] args) {
		//ask for language
	
		Scanner scanner = new Scanner(System.in);
		System.out.print("English (en) or German (de)? ");
		String input = scanner.next();	
		List<String> list = new ArrayList<String>(); 
		list.add("en");
		list.add("de");
//		Boolean b = false;
//		while(b.equals(false)){
//			if(!list.contains(input)){
//		    	System.out.print("Wrong Input, please type again: en or de? ");
//				input = scanner.next();
//			}
//			else{
//				b = true;
//			}
//		}
		scanner.close();
		
		

//	
		
		
		//get all filepaths
		PropertiesPipeline props = new PropertiesPipeline();
		Properties propsPipeline = props.loadProps();
		String locationProject = propsPipeline.getProperty("locationProject");
		props.writeProp("language", input, locationProject+"src/properties/example.properties");
		String language = input;
		String inputPipeline = propsPipeline.getProperty("inputPipeline");
		String outputPipeline = propsPipeline.getProperty("outputPipeline");
		
			
		
		//convert pdf files to xml
		pdf2xml px = new pdf2xml();
		px.convertPdftoXML(locationProject+inputPipeline+language+"/pdf", locationProject+outputPipeline+language+"/xml/");
		
		
		try {
		    Thread.sleep(1000);                 
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		
		//convert xml files to txt and correct spelling error
		xml2txt xt = new xml2txt();
		xt.totxt(locationProject+outputPipeline+language+"/xml", locationProject+outputPipeline+language+"/txtPreprocessed/");
		removeSpecialChars rsc = new removeSpecialChars();
		rsc.remove(locationProject+outputPipeline+language+"/txtPreprocessed",locationProject+outputPipeline+language+"/txt/" );
		
		//		textExtraction.PDFToTextConverter.generateTxtFiles(locationProject+inputPipeline+language+"/pdf/", locationProject+outputPipeline+language+"/txt/");

					
		//convert html to txt
		HtmlExtraction he = new HtmlExtraction();
		he.convertHtml2Txt(locationProject+inputPipeline+language+"/html/", 
				locationProject+outputPipeline+language+"/txt/");
		
		//get each sentence in one line
		SBD.sbd(locationProject+outputPipeline+language+"/txt", locationProject+outputPipeline+language+"/sentences/");
		
		//annotate the sentences
		Mate.MateEnglishGerman.mate(language);
			
		
		
		
		String[] s = null;
		try {
			in2rdf.Sentence2Triple.main(s, locationProject+outputPipeline+language+"/mate/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		
//		String[] s = null;
//		try {
//			in2rdf.Sentence2Triple.main(s, locationProject+outputPipeline+language+"/mate/");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
//		//extract the RDF triples from the annotation output
//		rdfExtraction re = new rdfExtraction();
//		re.main(locationProject+outputPipeline+language+"/mate/");
//		
		
	}	    
		
}
