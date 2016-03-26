package properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class PropertiesSesame {

	public Properties loadProps()
	   {
	      final Properties properties = new Properties();
	      String path = "/home/kathrin/workspace/sample-sesame-remote/src/main/java/properties/properties.properties";
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
	
}
			
		

			
			
