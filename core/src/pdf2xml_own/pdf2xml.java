package pdf2xml_own;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import properties.PropertiesPipeline;
import Helper.listFiles;

public class pdf2xml {

	public void convertPdftoXML(String input_path, String output_path){
		//uses pdf2xml (http://sourceforge.net/projects/pdf2xml/) 
		//for textextraction of pdf files
		PropertiesPipeline props = new PropertiesPipeline();
		Properties propsPipeline = props.loadProps();
		String extSrc = propsPipeline.getProperty("externalSourceCode");
		listFiles lf = new listFiles();
	    List<File> files = lf.listf(input_path);
	    for (File f : files){
	        try {
	        	String inputfile = f.getAbsolutePath();
	        	String outputfile = output_path+f.getAbsolutePath().split("/pdf/")[1].split("pdf")[0]+"xml";
	        	Runtime.getRuntime().exec(extSrc+"pdftoxml.linux64.exe.1.2_7 -noImage "+inputfile+" "+outputfile);
	        	System.out.println("Conversion of "+ inputfile.split("/pdf/")[1]+" into "+outputfile.split("/xml/")[1]+" done!");
	        	
	 
	        }
	        catch (IOException e) {
	            System.out.println("Exception happened :( ");
	            e.printStackTrace();
	            System.exit(-1);
	        }       
	    }
	}
	 
	public static void main(String[] args) {	
		}
}
