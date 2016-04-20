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
package pdf2xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import helper.listFiles;
import helper.readwriteFiles;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class pdf2xml {

	/**
	 * Convertes pdf to xml using pdf2xml (https://sourceforge.net/projects/pdf2xml/)
	 * 
	 * @param input_path
	 *            : absolute path of pdf input file
	 * @param output_path
	 *            : absolute path of xml output file
	 * @param locationProject
	 *            : absolute path of this project (TextAnalse)
	 * @throws InterruptedException
	 */
	public void convertPdftoXML(String input_path, String output_path,
			String locationProject) throws InterruptedException {
		listFiles lf = new listFiles();
		List<File> files = lf.listf(input_path);
		for (File f : files) {
			try {
				String inputfile = f.getAbsolutePath();
				String outputfile = output_path
						+ f.getAbsolutePath().split("/pdf/")[1].split("pdf")[0]
						+ "xml";
				Runtime.getRuntime().exec(
						locationProject
								+ "src/pdf2xml/pdftoxml.linux64.exe.1.2_7"
								+ " -noImage " + inputfile + " " + outputfile);

				System.out.println("Conversion of "
						+ inputfile.split("/pdf/")[1] + " into "
						+ outputfile.split("/xml/")[1] + " done!");

			} catch (IOException e) {
				System.out.println("Exception happened :( ");
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
/**
 * Validates xml files using xmllint
 * 
 * @param xmlDir: directory containing xml files
 * @throws InterruptedException
 */
	public void validateXML(String xmlDir) throws InterruptedException {
		// validate XML

		listFiles lf = new listFiles();
		List<File> files = lf.listf(xmlDir);

		for (File f : files) {
			try {
				String file = f.getAbsolutePath();
				Process rec = Runtime.getRuntime().exec(
						"xmllint --format --recover " + file);

				List<String> lines = new ArrayList<String>();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						rec.getInputStream()));

				String line;
				while ((line = in.readLine()) != null) {
					System.out.println(line);
					lines.add(line);
				}
				rec.waitFor();

				System.err.println("ok!");

				in.close();

				readwriteFiles rwf = new readwriteFiles();
				rwf.write(file, lines);
			} catch (IOException e) {
				System.out.println("Exception happened :( ");
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
	}
}