package com.ta.bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class readwriteFiles {
	
	public void write(String filepath, List<String> lines){
		try{
			FileWriter fileWriter = new FileWriter(filepath);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		    for(String l:lines){
		      	   bufferedWriter.write(l);
		    }    
		    bufferedWriter.close();
		    }
		    catch(FileNotFoundException ex) {
		    	 ex.printStackTrace();                
		    }
		    catch(IOException ex) {
		        ex.printStackTrace();
		    }
		}
	
	public List<String> read(String filepath){
		String line = null;
		List<String> lines = new ArrayList<String>();
	    try {
	    	 FileReader fileReader = new FileReader(filepath);
	         BufferedReader bufferedReader = new BufferedReader(fileReader);
             while((line = bufferedReader.readLine()) != null) {
                lines.add(line);
               //System.out.print(line+"\n");
            }
            bufferedReader.close();  
    		return lines;
	     }
	     catch(FileNotFoundException ex) {
	        	 ex.printStackTrace();                
	     }
	     catch(IOException ex) {
	         ex.printStackTrace();
	        }
		return lines;
	}
}
		