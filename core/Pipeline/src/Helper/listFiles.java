package Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class listFiles {
	
	 public List<File> listf(String dir) {
	        File directory = new File(dir);

	        List<File> resultList = new ArrayList<File>();

	        // get all the files from a directory
	        File[] fList = directory.listFiles();
	        resultList.addAll(Arrays.asList(fList));
	        for (File file : fList) {
	        	System.out.println(file.getAbsolutePath());
	        }
	        //System.out.println(fList);
	        return resultList;
	    }
	

}
