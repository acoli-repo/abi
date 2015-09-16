package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.joda.time.DateTime;

import stanfordCoreNLP.StanfordCoreferenceResolution;

/**
 * Add missing punctuations to end of sentences.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class AddMissingPunctuations {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		File srcDir = new File(args[0]);
		File destDir = new File(args[1]);
		File[] inputFiles = srcDir.listFiles();
		boolean parseWithCorefResInfos = Boolean.parseBoolean(args[2]);

		// line splitter for splitting line contents of input files
		String lineSplitter = "\\s{2,}|\\t+";

		// create new stanford coreference resolution instance
		StanfordCoreferenceResolution corefResolution = new stanfordCoreNLP.StanfordCoreferenceResolution(new Properties());

		// possible pos tags for verbs
		HashSet<String> verbPosTags = new HashSet<String>();
		verbPosTags.add("VB"); // Verb, base form
		verbPosTags.add("VBD"); // Verb, past tense
		verbPosTags.add("VBG"); // Verb, gerund or present participle
		verbPosTags.add("VBN"); // Verb, past participle
		verbPosTags.add("VBP"); // Verb, non-3rd person singular present
		verbPosTags.add("VBZ"); // Verb, 3rd person singular present

		// possible sentence endings
		HashSet<String> sentenceEndings = new HashSet<String>();
		sentenceEndings.add(".");
		sentenceEndings.add("!");
		sentenceEndings.add("?");
		sentenceEndings.add(";");

		int numInvalidSentences = 0;
		
		for (File inputFile : inputFiles) {
			
			// parse input file
			ArrayList<ArrayList<ArrayList<String>>> inputSentences = utility.ParserCoNLL.readCoNLLFile(inputFile, lineSplitter, parseWithCorefResInfos);
			ArrayList<ArrayList<ArrayList<String>>> outputSentences = new ArrayList<ArrayList<ArrayList<String>>>(1);
			
			for (ArrayList<ArrayList<String>> inputSentence : inputSentences) {
				ArrayList<ArrayList<ArrayList<String>>> tempSentences = new ArrayList<ArrayList<ArrayList<String>>>();
				tempSentences.add(inputSentence);
				int tryCount = 0;
				while (tryCount < 2) {
					try {
						// create sentence and tokens annotations
						StanfordCoreferenceResolution.createTokenSentenceAnnotations(tempSentences, true);
						outputSentences.add(tempSentences.get(0));
						break;
					} catch (Exception e) {
						e.printStackTrace();
						tryCount++;
						
						if (tryCount == 2){
							System.err.println("[ERROR] " + new DateTime() + ": Adding missing punctuation to file " + inputFile.getName() + " failed.");
							numInvalidSentences++;
							System.exit(0);
						}
						
						// check if sentence is missing punctuation
						String lastTokenInfo = inputSentence.get(inputSentence.size() - 1).get(3);
						if (!sentenceEndings.contains(lastTokenInfo)) {

							System.out.println("[INFO] " + new DateTime() + ": Adding missing punctuation to file " + inputFile.getName() + ".");
							
							int firstVerbIdx = 0;
							int tokenCount = 0;
							boolean sentenceContainsVerb = false;

							// determine first verb in sentence
							for (ArrayList<String> tokenInfos : inputSentence) {
								String pos = tokenInfos.get(3);
								if (verbPosTags.contains(pos)) {
									firstVerbIdx = tokenCount;
									sentenceContainsVerb = true;
									break;
								}
								tokenCount++;
							}
							// add punctuation to end of sentence
							ArrayList<String> newTokenInfos = new ArrayList<String>();
							newTokenInfos.add(String.valueOf(inputSentence.size() + 1));
							newTokenInfos.add(".");
							newTokenInfos.add(".");
							newTokenInfos.add(".");
							newTokenInfos.add("O");
							newTokenInfos.add("O");
							newTokenInfos.add("*))");
							if (sentenceContainsVerb) {
								newTokenInfos.add(String.valueOf(firstVerbIdx + 1));
							} else {
								newTokenInfos.add("0");
							}
							newTokenInfos.add("P");
							newTokenInfos.add("-");
							newTokenInfos.add("O");
							newTokenInfos.add("O");
							newTokenInfos.add("_");
							
							inputSentence.add(newTokenInfos);
							tempSentences.set(0, inputSentence);
						}
					}
				}
			}
			// write sentences to file
			File destFilePath = new File(destDir, inputFile.getName());
			ParserCoNLL.writeCoNLLFile(destFilePath, outputSentences, "\t", "\n");
		}
		System.out.println("[INFO] " + new DateTime() + ": Number of invalid sentences " + numInvalidSentences + ".");
	}
}