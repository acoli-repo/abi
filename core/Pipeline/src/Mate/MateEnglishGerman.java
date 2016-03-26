package Mate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import properties.PropertiesPipeline;

import Helper.listFiles;


public class MateEnglishGerman {
	
	
	public static void mate(String language){
		
		PropertiesPipeline props = new PropertiesPipeline();
		Properties propsPipeline = props.loadProps();
		String locationProject = propsPipeline.getProperty("locationProject");
		String extSrc = propsPipeline.getProperty("externalSourceCode");
		String outputPipeline = propsPipeline.getProperty("outputPipeline");
		String s = null;
		listFiles lf = new listFiles();
		List<File> files = lf.listf(locationProject+outputPipeline+language+"/sentences/");
	    String outputPath = locationProject+outputPipeline+language+"/mate/"; //path to the output folder
	    String langSpecCommand = "";
	    
	    if(language.equals("en")){
	    	langSpecCommand = "eng -tagger "+extSrc+"mate/models/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model" +
	        			" -parser "+extSrc+"mate/models/CoNLL2009-ST-English-ALL.anna-3.3.parser.model" +
	        			" -srl "+extSrc+"mate/models/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model" +
	        			" -lemma "+extSrc+"mate/models/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model ";
	    }
	    
	    else{
	    	langSpecCommand = "ger -tagger " + extSrc + "mate/models/german/tag-ger-3.6.model " +
	    			"-parser " + extSrc+"mate/models/german/parser-ger-3.6.model " +
	    			"-morph " + extSrc + "mate/models/german/morphology-ger-3.6.model " +
	    			"-srl " + extSrc + "mate/models/german/tiger-complete-predsonly-srl-4.11.srl.model " +
	    			"-lemma " + extSrc + "mate/models/german/lemma-ger-3.6.model ";	    			
	    }
	    
	    for (File f : files){
	    	try {	    		
	    		Process p = Runtime.getRuntime().exec("java -cp " 
	        			+
	        			extSrc+"mate/srl.jar:"+extSrc+"mate/lib/anna-3.3.jar:"+
	        			extSrc+"mate/lib/liblinear-1.51-with-deps.jar:"+extSrc+
	        			"mate/lib/opennlp-tools-1.4.3.jar:"+extSrc+"mate/lib/maxent-2.5.2.jar:" +
	        			extSrc+"mate/lib/trove.jar:"+extSrc+"mate/lib/seg.jar -Xmx3g " +
	        			"se/lth/cs/srl/CompletePipeline " +
	        			langSpecCommand +
	        			"-tokenize " +
	        			"-test " +
	        			f.getAbsolutePath() +
	        			" -out " +
	        			outputPath+f.getAbsolutePath().split("/sentences/")[1]	
	    				);
	    		
	    		
	        	System.out.println("Output saved to --> "+outputPath+f.getAbsolutePath().split("/sentences/")[1]);
	    		
	    		
	    		
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
	    }
		
		
	}
	
	 public static void main(String[] args) {
			
		}
	
	
}
