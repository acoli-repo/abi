package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.joda.time.DateTime;

/**
 * 
 * Parser class for reading and writing files in CoNLL format.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class ParserCoNLL {

	/**
	 * Returns parsed information, including or excluding coreference resolution information, extracted from input file in CoNLL format.
	 * 
	 * @param inputFile
	 *            File in CoNLL format.
	 * @param lineSplitter
	 *            Line splitter that is used for splitting line entries in the file. E.g. "\\s+" for space and tabular separated line entries.
	 * @param withCorefResInfos
	 *            Boolean value indicating whether coreference resolution information should be read from file as well or not.
	 * @return parsedDocument Information extracted from the input file.
	 */
	public static ArrayList<ArrayList<ArrayList<String>>> readCoNLLFile(File inputFile, String lineSplitter, boolean withCorefResInfos) {

		System.out.println("[INFO] " + new DateTime() + ": Reading file " + inputFile.getName() + ".");

		ArrayList<ArrayList<String>> parsedSentence = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<ArrayList<String>>> parsedDocument = new ArrayList<ArrayList<ArrayList<String>>>();

		// parse input file in CoNLL format
		BufferedReader bufferedReader = null;
		String inputCurrentLine = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(inputFile));
			while ((inputCurrentLine = bufferedReader.readLine()) != null) {

				// get parsed line
				ArrayList<String> parsedCurrentLine;
				if (withCorefResInfos) {
					parsedCurrentLine = new ArrayList<String>(Arrays.asList(inputCurrentLine.split(lineSplitter)));
				} else {
					parsedCurrentLine = new ArrayList<String>(Arrays.asList(inputCurrentLine.split("<")[0].split(lineSplitter)));
				}

				// store parsed line
				if (parsedCurrentLine.size() > 1) {
					String currentTokenNumber = parsedCurrentLine.get(0);
					if (!currentTokenNumber.equals("") && !currentTokenNumber.equals("<")) {
						if (currentTokenNumber.equalsIgnoreCase("1")) {
							parsedSentence = new ArrayList<ArrayList<String>>();
							parsedDocument.add(parsedSentence);
						}
						parsedSentence.add(parsedCurrentLine);

					} else { // add current line entries that are part from previous line to previous line

						ArrayList<String> entriesWrongLine = new ArrayList<String>();
						for (String element : parsedCurrentLine) {
							if (!element.equalsIgnoreCase("")) {
								entriesWrongLine.add(element);
							}
						}
						ArrayList<String> parsedPreviousLine = parsedSentence.get(parsedSentence.size() - 1);
						parsedPreviousLine.addAll(entriesWrongLine);
						parsedSentence.set(parsedSentence.size() - 1, parsedPreviousLine);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return parsedDocument;
	}

	/**
	 * Writes annotated text to a file in CoNLL format.
	 * 
	 * @param destFilePath
	 *            File path of the destination file.
	 * @param parsedDocument
	 *            Annotated text.
	 * @param lineSplitter
	 *            Line splitter that is used for splitting the line entries in the destination file, e.g. "\t" for tabulator.
	 * @param lineBreak
	 *            Line break that is used to start a new line, e.g. "\n".
	 */
	public static void writeCoNLLFile(File destFilePath, ArrayList<ArrayList<ArrayList<String>>> parsedDocument, String lineSplitter, String lineBreak) {

		System.out.println("[INFO] " + new DateTime() + ": Writing annotations to file " + destFilePath.getName() + ".");
		
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(destFilePath);

			for (Iterator<ArrayList<ArrayList<String>>> documentItr = parsedDocument.iterator(); documentItr.hasNext();) {

				ArrayList<ArrayList<String>> sentence = documentItr.next();
				for (Iterator<ArrayList<String>> sentenceItr = sentence.iterator(); sentenceItr.hasNext();) {

					ArrayList<String> tokenInfos = sentenceItr.next();
					for (Iterator<String> tokenInfosItr = tokenInfos.iterator(); tokenInfosItr.hasNext();) {

						String tokenInfo = tokenInfosItr.next();
						fileWriter.append(tokenInfo);
						if (tokenInfosItr.hasNext()) {
							fileWriter.append(lineSplitter);
						}
					}
					if (sentenceItr.hasNext()) {
						fileWriter.append(lineBreak);
					}
				}
				if (documentItr.hasNext()) {
					fileWriter.append(lineBreak + lineBreak);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}