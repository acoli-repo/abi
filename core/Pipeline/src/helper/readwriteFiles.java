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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class readwriteFiles {
	/**
	 * 
	 * @param filepath absolute path to the file where to write to
	 * @param lines list of string (the lines which are to write to the file)
	 */

	public void write(String filepath, List<String> lines) {
		try {
			FileWriter fileWriter = new FileWriter(filepath);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			for (String l : lines) {
				bufferedWriter.write(l);
			}
			bufferedWriter.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
/**
 * 
 * @param filepath: absolute path of file to be read
 * @return list of strings (lines of the file)
 */
	public List<String> read(String filepath) {
		String line = null;
		List<String> lines = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
				// System.out.print(line+"\n");
			}
			bufferedReader.close();
			return lines;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return lines;
	}
/**
 * 
 * @param filepath: absolute path of the file to be read
 * @return first line of the file (String)
 */
	public String readFirstLine(String filepath) {
		String line = null;
		String returndefault = "";
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				if (line.charAt(0) == '<') {
					System.out.println("HTML FOUND");
					return "html";
				}
				if (line.startsWith("%PDF")) {
					System.out.println("PDF FOUND");
					return "pdf";

				} else {
					System.out.println("File format could not be identified");
					break;
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return returndefault;
	}

}
