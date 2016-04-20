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
package properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import helper.readwriteFiles;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class PropertiesPipeline {
	/**
	 * Loads the properties file
	 * 
	 * @param propsfile: absolute path of properties file
	 * @return properties
	 */
	public Properties loadProps(String propsfile) {
		final Properties properties = new Properties();
		String path = propsfile;
		try {
			final FileInputStream in = new FileInputStream(path);
			properties.load(in);
			in.close();
		} catch (FileNotFoundException fnfEx) {
			System.err.println("Could not read properties from file " + path);
		} catch (IOException ioEx) {
			System.err.println("IOException encountered while reading from "
					+ path);
		}
		return properties;
	}

	/**
	 * Write a new property to a properties file
	 * 
	 * @param k: the key of the property
	 * @param v: the value of the property
	 * @param filepath: the absolute path of the properties file
	 */
	public void writeProp(String k, String v, String filepath) {
		readwriteFiles wf = new readwriteFiles();
		List<String> lines = wf.read(filepath);
		List<String> final_lines = new ArrayList<String>();
		for (String l : lines) {
			if (l.split("=")[0].equals(k)) {
				final_lines.add(k + "=" + v + "\n");
			} else {
				final_lines.add(l + "\n");
			}
		}
		wf.write(filepath, final_lines);
	}
}
