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
package html2txt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import helper.listFiles;
import helper.readwriteFiles;

/**
 * 
 * @author Kathrin Donandt, Zhanhong Huang
 *
 */
public class HtmlExtraction {

	/**
	 * Gets the content.
	 *
	 * @param url
	 *            : the url of a html file
	 * @return the content of html file as String
	 */
	public String getContent(String url) {
		Document doc;
		String content = "";
		try {
			doc = Jsoup.connect(url).get();
			Elements ps = doc.select("p");
			content = ps.text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	/**
	 * Parses the document from file.
	 *
	 * @param htmlfile
	 *            : the htmlfile (absolute path)
	 * @return a list of lines (strings), each representing the content of a tag
	 */
	public List<String> parseDocumentFromFile(String htmlfile) {
		File input = new File(htmlfile);
		Document doc;
		String text = "";
		List<String> lines = new ArrayList<String>();
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

			// get local html content

			org.jsoup.nodes.Element html = doc.select("html").first();
			String id = html.id();
			try {
				org.jsoup.nodes.Element article = doc.select("article").first();
				Elements ptext = article.children();
				article.getElementsByTag("p");
				for (org.jsoup.nodes.Element ts : ptext) {
					text = ts.text();
					System.out.println(ts.text());
					lines.add(text);
				}
			} catch (Exception e) {
				org.jsoup.nodes.Element content = doc.select("html").first();
				Elements ptext1 = content.children();
				content.getElementsByTag("p");
				// org.jsoup.nodes.Element content = doc.getElementById(id);
				// Elements ptext = content.getElementsByTag("p");
				for (org.jsoup.nodes.Element ts : ptext1) {
					text = ts.text();
					System.out.println(ts.text());
					lines.add(text);
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return lines;

	}

	/**
	 * Convert html to txt.
	 *
	 * @param input_path
	 *            : absolute path of input file (html)
	 * @param output_path
	 *            : absolute path ouf output file (txt)
	 */
	public void convertHtml2Txt(String input_path, String output_path) {
		listFiles lf = new listFiles();
		List<File> files = lf.listf(input_path);
		for (File f : files) {

			String inputfile = f.getAbsolutePath();
			String outputfile = output_path
					+ f.getAbsolutePath().split("/html/")[1].split("html")[0]
					+ "txt";
			List<String> lines = parseDocumentFromFile(inputfile);
			readwriteFiles rw = new readwriteFiles();
			rw.write(outputfile, lines);
			System.out.println("Conversion of " + inputfile.split("/html/")[1]
					+ " into " + outputfile.split("/txt/")[1] + " done!");

		}
	}

	/**
	 * The main method (for testing)
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		HtmlExtraction he = new HtmlExtraction();
		he.parseDocumentFromFile("/home/kathrin/workspace/PraktikumWS1516/localFiles/inputPipeline/en/html/telegraph.html");

	}

}
