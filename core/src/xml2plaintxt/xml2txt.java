package xml2plaintxt;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import properties.PropertiesPipeline;

import java.io.*;
import java.util.List;
import java.util.Properties;

import Helper.listFiles;


public class xml2txt {
		//use xsl file to extract the text out of the xml file
	    public void totxt(String input_path, String output_path) {
	    	listFiles lf = new listFiles();
	  	   	List<File> files = lf.listf(input_path);
	  	   	PropertiesPipeline props = new PropertiesPipeline();
	  	   	Properties propsPipeline = props.loadProps();
	  	   	String extSrc = propsPipeline.getProperty("externalSourceCode");
	  	   	String xslFile = extSrc+"xslFile.xsl";
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

		public static void main(String[] args) {	
			}
}


	 

