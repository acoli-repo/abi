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
package sbd;

import java.io.*;
import java.util.List;
import helper.listFiles;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class SBD {

	/**
	 * Runs the SBD program Splitta (https://pypi.python.org/pypi/splitta/0.1.0)
	 *
	 * @param input_path : absolute path of input file
	 * @param output_path : absolute path of output file
	 */
	public static void sbd(String input_path, String output_path, String locationSBD) {

		String s = null;
		listFiles lf = new listFiles();
		List<File> files = lf.listf(input_path);

		for (File f : files) {// File Namen dürfen keine Leerzeichen enthalten!!
			try {
				Process p = Runtime.getRuntime().exec(
						"python " + locationSBD+"splitta/sbd.py" + " " + "-m "
								+ locationSBD+"splitta/model_nb" + " "
								+ f.getAbsolutePath() + " -o " + output_path
								+ f.getAbsolutePath().split("/txt/")[1]);
				System.out.println("Output saved to --> " + output_path
						+ f.getAbsolutePath().split("/txt/")[1]);

				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));

				// read the output from the command
				System.out
						.println("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}

				// read any errors from the attempted command
				System.out
						.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}
				// System.exit(0);
			} catch (IOException e) {
				System.out.println("exception happened - here's what I know: ");
				e.printStackTrace();
				System.exit(-1);
			}
		}// for loop
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	}
}
