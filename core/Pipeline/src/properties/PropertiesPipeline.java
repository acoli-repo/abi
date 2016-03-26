package properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import Helper.readwriteFiles;

public class PropertiesPipeline {

	public Properties loadProps()
	   {
	      final Properties properties = new Properties();
	      String path = "/home/kathrin/workspace/PraktikumWS1516/src/properties/example.properties";
	      try
	      {
	         final FileInputStream in = new FileInputStream(path);
	         properties.load(in);
	         in.close();
	      }
	      catch (FileNotFoundException fnfEx)
	      {
	         System.err.println("Could not read properties from file " + path);
	      }
	      catch (IOException ioEx)
	      {
	         System.err.println(
	            "IOException encountered while reading from " + path);
	      }
	      return properties;
	}
	
	
	public void writeProp(String k, String v, String filepath){	
        readwriteFiles wf = new readwriteFiles();
		List<String> lines = wf.read(filepath);
		List<String> final_lines = new ArrayList<String>();
		for(String l:lines){
			if (l.split("=")[0].equals(k)){
				final_lines.add(k+"="+v+"\n");
			}
			else{
				final_lines.add(l+"\n");
			}
		}     
        wf.write(filepath, final_lines);
	}
}
			
		

			
			