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

package helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author kathrin
 *
 */
public class listFiles {
	/**
	 * 
	 * @param dir
	 *            absolute path of directory which files' should be listed
	 * @return list of files
	 */

	public List<File> listf(String dir) {
		File directory = new File(dir);

		List<File> resultList = new ArrayList<File>();

		// get all the files from a directory
		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		for (File file : fList) {
			System.out.println(file.getAbsolutePath());
		}
		// System.out.println(fList);
		return resultList;
	}

}
