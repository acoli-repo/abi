package utility;

import java.io.File;
import java.util.ArrayList;

/**
 * Calculates number of sentences and token of a document corpus.
 * @author Sergej Jaschonkow
 *
 */

public class CorpusSize {

	/**
	 * Calculates number of sentences and token of a document corpus.
	 * @param args
	 */
	public static void main(String[] args){
		
		File srcDir = new File(args[0]);
		File[] inputFiles = srcDir.listFiles();
	
		// line splitter for splitting line contents of input files
		String lineSplitter = "\\s{2,}|\\t+";
		
		int totalNumSentences = 0;
		int totalNumTokens = 0;
		
		for (File inputFile : inputFiles) {
			
			// parse input file
			ArrayList<ArrayList<ArrayList<String>>> inputSentences = utility.ParserCoNLL.readCoNLLFile(inputFile, lineSplitter, true);
			
			// calculate number of sentences and tokens in current file
			int numTokens = utility.util.calculateNumTokens(inputSentences);
			int numSentences = inputSentences.size();
			
			// update total number of sentences and tokens
			totalNumTokens += numTokens; 
			totalNumSentences += numSentences;
		}
		System.out.println("Number of sentences: " + totalNumSentences);
		System.out.println("Number of token: " + totalNumTokens);
	}
}