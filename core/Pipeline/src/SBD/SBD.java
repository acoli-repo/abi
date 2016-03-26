package SBD;
import java.io.*;
import java.util.List;
import java.util.Properties;

import properties.PropertiesPipeline;
import Helper.listFiles;

public class SBD {
	
	
	public static void sbd(String input_path, String output_path) {
		 
       String s = null;
       PropertiesPipeline props = new PropertiesPipeline();
       Properties propsPipeline = props.loadProps();
       listFiles lf = new listFiles();
	   List<File> files = lf.listf(input_path);
       
       //String language = propsPipeline.getProperty("language");
       //String locationProject = propsPipeline.getProperty("locationProject");
       //String outputPipeline = propsPipeline.getProperty("outputPipeline");
       String extSrc = propsPipeline.getProperty("externalSourceCode"); 
       
       
     //  List<File> files = lf.listf(locationProject+outputPipeline+language+"/txt/");//path to folder containing the plain txt files
      // String outputPath = locationProject+outputPipeline+language+"/sentences/"; //path to the output folder
       
       for (File f : files){//File Namen dürfen keine Leerzeichen enthalten!!
	        try {
	        	Process p = Runtime.getRuntime().exec("python " +
	        			extSrc+"splitta/sbd.py " +//path to python file
	        			"-m "+extSrc+"splitta/model_nb " +
	        			f.getAbsolutePath() + " -o " + output_path+f.getAbsolutePath().split("/txt/")[1]);
	        	System.out.println("Output saved to --> "+output_path+f.getAbsolutePath().split("/txt/")[1]);
	       
	            BufferedReader stdInput = new BufferedReader(new
	                 InputStreamReader(p.getInputStream()));
	 
	            BufferedReader stdError = new BufferedReader(new
	                 InputStreamReader(p.getErrorStream()));
	 
	            // read the output from the command
	            System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
	             
	            // read any errors from the attempted command
	            System.out.println("Here is the standard error of the command (if any):\n");
	            while ((s = stdError.readLine()) != null) {
	                System.out.println(s);
	            }     
	          //  System.exit(0);
	        }
	        catch (IOException e) {
	            System.out.println("exception happened - here's what I know: ");
	            e.printStackTrace();
	            System.exit(-1);
	        }       
       }//for loop
    }
	 public static void main(String[] args) {	
		}
}
