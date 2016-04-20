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
package pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import properties.PropertiesPipeline;
import helper.listFiles;
import helper.readwriteFiles;
import org.apache.commons.io.FileUtils;
import pdf2xml.pdf2xml;
import sbd.*;
import xml2plaintxt.*;
import html2txt.HtmlExtraction;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class Pipeline {
	// public static void main(String[] args){
	// //propsfile is the path to the properties file (configuration file)
	// String propsfile = args[0];
	// //language
	// String input = args[1];
	/**
	 * Run the pipeline; ! Change the absolute path to properties file
	 * example.properties ! Choose a language: "en" (English") or "de" (German)
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String propsfile = "/home/kathrin/Dokumente/Pipeline/src/properties/example.properties";
		String input = "de";
		run(propsfile, input);
	}

	/**
	 * Runs the whole pipeline
	 * 
	 * @param propsfile
	 *            : absolute path of properties file example.properties (in
	 *            src/properties/)
	 * @param input
	 *            : language ("en" or "de")
	 */
	public static void run(String propsfile, String input) {

		// get all filepaths
		PropertiesPipeline props = new PropertiesPipeline();
		// Properties propsPipeline =
		Properties p = props.loadProps(propsfile);

		String language = input;
		String inputPipeline = p.getProperty("inputPipeline");
		String outputPipeline = p.getProperty("outputPipeline");

		String locationProject = p.getProperty("locationProject");
		String filePool = inputPipeline + language + File.separator
				+ "filePool";

		props.writeProp("language", input, propsfile);

		listFiles lf = new listFiles();
		readwriteFiles rwf = new readwriteFiles();

		List<File> filePoolFiles = lf.listf(filePool);

		for (File f : filePoolFiles) {

			// replace whitespaces in inputfilenames
			File newFile = new File(f.getParent(), f.getName()
					.replace(" ", "_"));
			try {
				Files.move(f.toPath(), newFile.toPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.err.println(f.getAbsoluteFile() + " renamed to "
					+ newFile.getAbsolutePath());

			// check file type and sort into pdf and html directories

			Path fpath = Paths.get(newFile.getAbsolutePath());
			String fpath_string = newFile.getAbsolutePath();
			String filename = newFile.getName();
			String fileformat = rwf.readFirstLine(fpath_string);
			Path destination = Paths.get(inputPipeline + language
					+ File.separator + fileformat + File.separator + filename);
			try {
				Files.move(fpath, destination,
						StandardCopyOption.REPLACE_EXISTING);
				// Files.copy(fpath, destination,
				// StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// delete old files and filepool
		File dir1 = new File(outputPipeline + language + "/txtPreprocessed/");
		File dir2 = new File(outputPipeline + language + "/txt/");
		File dir3 = new File(outputPipeline + language + "/sentences/");
		File dir4 = new File(outputPipeline + language + "/mate/");
		File dir5 = new File(outputPipeline + language + "/xml/");
		File dir6 = new File(outputPipeline + language + "/rdf/");
		File dir7 = new File(filePool);
		File dir8 = new File(outputPipeline + language + "/nquads/");
		try {
			FileUtils.cleanDirectory(dir1);
			FileUtils.cleanDirectory(dir2);
			FileUtils.cleanDirectory(dir3);
			FileUtils.cleanDirectory(dir4);
			FileUtils.cleanDirectory(dir5);
			FileUtils.cleanDirectory(dir6);
			FileUtils.cleanDirectory(dir7);
			FileUtils.cleanDirectory(dir8);

			System.out.println("\n\n\nFINISHED!!!!!\n\n\n");

			// convert pdf files to xml
			pdf2xml px = new pdf2xml();
			try {
				px.convertPdftoXML(inputPipeline + language + "/pdf",
						outputPipeline + language + "/xml/", locationProject);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// validate xml files
			try {
				px.validateXML(outputPipeline + language + "/xml/");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// convert xml files to txt and correct spelling error
			xml2txt xt = new xml2txt();
			xt.totxt(outputPipeline + language + "/xml", outputPipeline
					+ language + "/txtPreprocessed/", locationProject);
			removeSpecialChars rsc = new removeSpecialChars();
			rsc.remove(outputPipeline + language + "/txtPreprocessed",
					outputPipeline + language + "/txt/");

			// convert html to txt
			HtmlExtraction he = new HtmlExtraction();
			he.convertHtml2Txt(inputPipeline + language + "/html/",
					outputPipeline + language + "/txt/");

			// get each sentence in one line
			String locationSBD = p.getProperty("locationSBD");
			SBD.sbd(outputPipeline + language + "/txt", outputPipeline
					+ language + "/sentences/", locationSBD);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// annotate the sentences
		try {
			mate.mateEnglishGermanShell.mate(language, propsfile);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String[] s = null;
		try {
			in2rdf.Sentence2Triple.main(s,
					outputPipeline + language + "/mate/", locationProject,
					language);
		} catch (Exception e) {
			System.out.println("Error in RDF Extraction");
			e.printStackTrace();
		}

	}

}
